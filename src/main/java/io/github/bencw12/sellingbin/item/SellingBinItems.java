package io.github.bencw12.sellingbin.item;

import io.github.bencw12.sellingbin.SellingBin;
import io.github.bencw12.sellingbin.block.SellingBinBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

public class SellingBinItems {
    public static RegistryObject<CreativeModeTab> SELLING_BIN_TAB;
    public static RegistryObject<Item> SELLING_BIN_ITEM;
    public static RegistryObject<Item> EMERALD_NUGGET_ITEM;

    public static void register() {
        SELLING_BIN_ITEM = SellingBin.ITEMS.register("selling_bin",
            () -> new BlockItem(SellingBinBlocks.SELLING_BIN.get(), new Item.Properties()));
        EMERALD_NUGGET_ITEM = SellingBin.ITEMS.register("emerald_nugget",
                () -> new Item(new Item.Properties().stacksTo(64)));

        SELLING_BIN_TAB = SellingBin.CREATIVE_MODE_TABS.register("selling_bin", () -> CreativeModeTab.builder()
                .withTabsBefore(CreativeModeTabs.allTabs().get(CreativeModeTabs.allTabs().size() - 1).getTabsImage())
                .icon(() -> SELLING_BIN_ITEM.get().getDefaultInstance())
                .title(Component.translatable("block.selling_bin.selling_bin"))
                .displayItems((parameters, output) -> {
                    output.accept(SELLING_BIN_ITEM.get());
                    output.accept(EMERALD_NUGGET_ITEM.get());
                }).build());
    }
}
