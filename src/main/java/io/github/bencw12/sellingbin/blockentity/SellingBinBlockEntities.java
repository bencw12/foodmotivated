package io.github.bencw12.sellingbin.blockentity;

import io.github.bencw12.sellingbin.SellingBin;
import io.github.bencw12.sellingbin.block.SellingBinBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

public class SellingBinBlockEntities {
    public static RegistryObject<BlockEntityType<SellingBinBlockEntity>> SELLING_BIN_BLOCK_ENTITY;

    public static void register() {
        SELLING_BIN_BLOCK_ENTITY =
                SellingBin.BLOCK_ENTITIES.register("selling_bin_block_entity",
                        () -> BlockEntityType.Builder.of(SellingBinBlockEntity::new,
                                SellingBinBlocks.SELLING_BIN.get()).build(null));
    }
}
