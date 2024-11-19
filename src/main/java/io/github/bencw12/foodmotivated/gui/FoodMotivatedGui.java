package io.github.bencw12.foodmotivated.gui;

import io.github.bencw12.foodmotivated.FoodMotivated;
import io.github.bencw12.foodmotivated.gui.inventory.SellingBinMenu;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.RegistryObject;

public class FoodMotivatedGui {
    public static RegistryObject<MenuType<SellingBinMenu>> SELLING_BIN_MENU;

    public static void register() {
        SELLING_BIN_MENU = FoodMotivated.MENUS.register("selling_bin",
                () -> new MenuType<>(SellingBinMenu::new, FeatureFlags.DEFAULT_FLAGS));
    }
}
