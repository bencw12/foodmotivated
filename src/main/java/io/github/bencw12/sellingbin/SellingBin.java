package io.github.bencw12.sellingbin;

import io.github.bencw12.sellingbin.block.SellingBinBlocks;
import io.github.bencw12.sellingbin.blockentity.SellingBinBlockEntities;
import io.github.bencw12.sellingbin.client.renderer.blockentity.SellingBinBlockEntityRenderer;
import io.github.bencw12.sellingbin.data.SellingBinDataManager;
import io.github.bencw12.sellingbin.data.SellingBinOffersSavedData;
import io.github.bencw12.sellingbin.gui.SellingBinGui;
import io.github.bencw12.sellingbin.gui.screens.inventory.SellingBinScreen;
import io.github.bencw12.sellingbin.item.SellingBinItems;
import io.github.bencw12.sellingbin.network.SellingBinPacketHandler;
import io.github.bencw12.sellingbin.world.item.SellingBinOffers;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

@Mod(SellingBin.MOD_ID)
public class SellingBin
{
    public static final String MOD_NAME = "Selling Bin";
    public static final String MOD_ID = "selling_bin";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MOD_ID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MOD_ID);
    public static SellingBinOffersSavedData OFFERS_SAVE;


    public SellingBin(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::commonSetup);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        MENUS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        SellingBinBlocks.register();
        SellingBinBlockEntities.register();
        SellingBinItems.register();
        SellingBinGui.register();
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        LOGGER.info(MOD_NAME + ": common setup");

        // register packet types
        SellingBinPacketHandler.register();
    }

    @Mod.EventBusSubscriber(modid = MOD_ID)
    public static class ServerModEvents
    {

        @SubscribeEvent
        public static void onWorldLoad(ServerStartedEvent event) {
            LOGGER.info(MOD_NAME + ": server starting");
            for (ServerLevel world : event.getServer().getAllLevels()) {
                if (world.dimension() == Level.OVERWORLD) {
                    LOGGER.info(MOD_NAME + ": loading selling bin offers");
                    OFFERS_SAVE = SellingBinDataManager.getOffers(world);

                    if (OFFERS_SAVE.getOffers().isEmpty()) {
                        SellingBinOffers.getNewOffers();
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                SellingBinDataManager.sendOffersToClient(player);
            }
        }

        private static long lastTick = -1;
        @SubscribeEvent
        public static void onServerTick(TickEvent.LevelTickEvent event) {
            if (event.phase == TickEvent.Phase.END && event.level instanceof ServerLevel serverLevel) {
                if (serverLevel.dimension() == Level.OVERWORLD) {
                    long time = serverLevel.getDayTime() % 24000;

                    if (lastTick > time) {
                        SellingBinOffers.getNewOffers();
                    }

                    lastTick = time;

                    // send to all players
                    for (ServerPlayer player : serverLevel.players()) {
                        SellingBinDataManager.sendOffersToClient(player);
                    }
                }
            }
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info(MOD_NAME + ": client setup");
            ItemBlockRenderTypes.setRenderLayer(SellingBinBlocks.SELLING_BIN.get(), RenderType.cutout());

            event.enqueueWork(
                    () -> MenuScreens.register(SellingBinGui.SELLING_BIN_MENU.get(), SellingBinScreen::new)
            );
        }

        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            LOGGER.info("Registering renderers");
            event.registerBlockEntityRenderer(SellingBinBlockEntities.SELLING_BIN_BLOCK_ENTITY.get(), SellingBinBlockEntityRenderer::new);
        }

    }
}
