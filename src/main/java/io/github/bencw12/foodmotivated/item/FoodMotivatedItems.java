package io.github.bencw12.foodmotivated.item;

import io.github.bencw12.foodmotivated.FoodMotivated;
import io.github.bencw12.foodmotivated.block.FoodMotivatedBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

public class FoodMotivatedItems {
    public static RegistryObject<CreativeModeTab> SELLING_BIN_TAB;
    public static RegistryObject<Item> SELLING_BIN_ITEM;
    public static RegistryObject<Item> EMERALD_NUGGET_ITEM;

    public static void register() {
        SELLING_BIN_ITEM = FoodMotivated.ITEMS.register("selling_bin",
            () -> new BlockItem(FoodMotivatedBlocks.SELLING_BIN.get(), new Item.Properties()));
        EMERALD_NUGGET_ITEM = FoodMotivated.ITEMS.register("emerald_nugget",
                () -> new Item(new Item.Properties().stacksTo(64)));

        SELLING_BIN_TAB = FoodMotivated.CREATIVE_MODE_TABS.register("foodmotivated", () -> CreativeModeTab.builder()
                .withTabsBefore(CreativeModeTabs.allTabs().get(CreativeModeTabs.allTabs().size() - 1).getTabsImage())
                .icon(() -> SELLING_BIN_ITEM.get().getDefaultInstance())
                .title(Component.translatable("block.foodmotivated.selling_bin"))
                .displayItems((parameters, output) -> {
                    output.accept(SELLING_BIN_ITEM.get());
                    output.accept(EMERALD_NUGGET_ITEM.get());
                }).build());
    }
}
