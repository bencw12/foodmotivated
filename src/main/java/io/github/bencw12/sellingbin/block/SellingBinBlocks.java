package io.github.bencw12.sellingbin.block;

import io.github.bencw12.sellingbin.SellingBin;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

public class SellingBinBlocks {
    public static RegistryObject<Block> SELLING_BIN;

    public static void register() {
        SELLING_BIN = SellingBin.BLOCKS.register("selling_bin", SellingBinBlock::new);
    }
}
