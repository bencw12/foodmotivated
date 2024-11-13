package io.github.bencw12.sellingbin.gui;

import io.github.bencw12.sellingbin.SellingBin;
import io.github.bencw12.sellingbin.gui.inventory.SellingBinMenu;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.RegistryObject;

public class SellingBinGui {
    public static RegistryObject<MenuType<SellingBinMenu>> SELLING_BIN_MENU;

    public static void register() {
        SELLING_BIN_MENU = SellingBin.MENUS.register("selling_bin",
                () -> new MenuType<>(SellingBinMenu::new, FeatureFlags.DEFAULT_FLAGS));
    }
}
