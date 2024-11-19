package io.github.bencw12.foodmotivated.blockentity;

import io.github.bencw12.foodmotivated.FoodMotivated;
import io.github.bencw12.foodmotivated.block.FoodMotivatedBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

public class FoodMotivatedBlockEntities {
    public static RegistryObject<BlockEntityType<SellingBinBlockEntity>> SELLING_BIN_BLOCK_ENTITY;

    public static void register() {
        SELLING_BIN_BLOCK_ENTITY =
                FoodMotivated.BLOCK_ENTITIES.register("selling_bin_block_entity",
                        () -> BlockEntityType.Builder.of(SellingBinBlockEntity::new,
                                FoodMotivatedBlocks.SELLING_BIN.get()).build(null));
    }
}
