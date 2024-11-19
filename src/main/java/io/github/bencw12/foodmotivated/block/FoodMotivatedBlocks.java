package io.github.bencw12.foodmotivated.block;

import io.github.bencw12.foodmotivated.FoodMotivated;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

public class FoodMotivatedBlocks {
    public static RegistryObject<Block> SELLING_BIN;

    public static void register() {
        SELLING_BIN = FoodMotivated.BLOCKS.register("selling_bin", SellingBinBlock::new);
    }
}
