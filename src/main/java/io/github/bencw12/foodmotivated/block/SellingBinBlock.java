package io.github.bencw12.foodmotivated.block;

import io.github.bencw12.foodmotivated.blockentity.FoodMotivatedBlockEntities;
import io.github.bencw12.foodmotivated.blockentity.SellingBinBlockEntity;
import io.github.bencw12.foodmotivated.gui.inventory.SellingBinMenu;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class SellingBinBlock extends BaseEntityBlock {

    public static final IntegerProperty LEVEL_SELLING_BIN =
            IntegerProperty.create("level", 0, 8);
    private static final Property<Integer> LEVEL;
    private static final VoxelShape OUTER_SHAPE;
    private static final VoxelShape[] SHAPES;

    public SellingBinBlock() {
        super(Properties.copy(Blocks.COMPOSTER).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    static {
        LEVEL = LEVEL_SELLING_BIN;
        OUTER_SHAPE = Shapes.block();
        SHAPES = Util.make(new VoxelShape[9], (shapes) -> {
            for(int i = 0; i < 8; ++i) {
                shapes[i] = Shapes.join(OUTER_SHAPE, Block.box(2.0, Math.max(2, 1 + i * 2), 2.0, 14.0, 16.0, 14.0), BooleanOp.ONLY_FIRST);
            }

            shapes[8] = shapes[7];
        });
    }

    public @NotNull VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(LEVEL)];
    }

    public VoxelShape getInteractionShape(BlockState state, BlockGetter getter, BlockPos pos) {
        return OUTER_SHAPE;
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return SHAPES[0];
    }

    public InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SellingBinBlockEntity sellingBinBlockEntity) {
                NetworkHooks.openScreen((ServerPlayer) player, new SimpleMenuProvider(
                        (id, playerInventory, p) -> {
                            SellingBinMenu menu = new SellingBinMenu(id, playerInventory, sellingBinBlockEntity.getInventory());
                            menu.setContainer(sellingBinBlockEntity);
                            return menu;
                        },
                        Component.translatable("block.foodmotivated.selling_bin")
                ), pos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    public void onRemove(BlockState start, Level level, BlockPos pos, BlockState state, boolean b) {
        if (!start.is(state.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof Container container) {
                Containers.dropContents(level, pos, container);
                level.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(start, level, pos, state, b);
        }
    }

    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rng) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof SellingBinBlockEntity sb) {
            sb.recheckOpen();
        }
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new SellingBinBlockEntity(pos, state);
    }

    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> t) {
        return t == FoodMotivatedBlockEntities.SELLING_BIN_BLOCK_ENTITY.get() ? SellingBinBlockEntity::tick : null;
    }
}
