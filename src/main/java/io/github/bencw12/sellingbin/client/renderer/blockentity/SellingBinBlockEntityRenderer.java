package io.github.bencw12.sellingbin.client.renderer.blockentity;

import io.github.bencw12.sellingbin.blockentity.SellingBinBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.Collections;

public class SellingBinBlockEntityRenderer implements BlockEntityRenderer<SellingBinBlockEntity> {
    private final Vec3[] RENDER_STACKS = {
            new Vec3(0.4, 0.2f, 0.4f),
            new Vec3(0.6, 0.2f, 0.7f),
            new Vec3(0.4, 0.3f, 0.6f),
            new Vec3(0.6, 0.3f, 0.4f),
            new Vec3(0.5, 0.4f, 0.3f),
            new Vec3(0.5, 0.4f, 0.7f),
            new Vec3(0.3, 0.55f, 0.5f),
            new Vec3(0.7, 0.55f, 0.5f),
            new Vec3(0.5, 0.6f, 0.5f),
            new Vec3(0.3, 0.65f, 0.3f),
            new Vec3(0.7, 0.65f, 0.3f),
            new Vec3(0.7, 0.65f, 0.7f),
            new Vec3(0.3, 0.7f, 0.7f),
            new Vec3(0.5, 0.75f, 0.5f),
            new Vec3(0.3, 0.8f, 0.7f),
            new Vec3(0.3, 0.8f, 0.3f),
            new Vec3(0.7, 0.8f, 0.3f),
            new Vec3(0.7, 0.85f, 0.7f),
            new Vec3(0.35, 0.9f, 0.65f),
            new Vec3(0.35, 0.95f, 0.35f),
            new Vec3(0.65, 0.95, 0.35f),
            new Vec3(0.65, 1.0f, 0.65f),
            new Vec3(0.4, 0.9f, 0.6f),
            new Vec3(0.4, 0.95f, 0.4f),
            new Vec3(0.6, 0.95, 0.4f),
            new Vec3(0.6, 1.0f, 0.6f),
    };

    private final Quaternionf[][] RENDER_ROTS = {
            {Axis.XP.rotationDegrees(90), Axis.YP.rotationDegrees(0), Axis.ZP.rotationDegrees(0)},
            {Axis.XP.rotationDegrees(90), Axis.YP.rotationDegrees(350), Axis.ZP.rotationDegrees(60)},
            {Axis.XP.rotationDegrees(90), Axis.YP.rotationDegrees(10), Axis.ZP.rotationDegrees(45)},
            {Axis.XP.rotationDegrees(30), Axis.YP.rotationDegrees(0), Axis.ZP.rotationDegrees(45)},
            {Axis.XP.rotationDegrees(40), Axis.YP.rotationDegrees(-10), Axis.ZP.rotationDegrees(45)},
            {Axis.XP.rotationDegrees(-40), Axis.YP.rotationDegrees(-15), Axis.ZP.rotationDegrees(45)},
            {Axis.XP.rotationDegrees(90), Axis.YP.rotationDegrees(10), Axis.ZP.rotationDegrees(-45)},
            {Axis.XP.rotationDegrees(90), Axis.YP.rotationDegrees(-15), Axis.ZP.rotationDegrees(-45)},
            {Axis.XP.rotationDegrees(90), Axis.YP.rotationDegrees(0), Axis.ZP.rotationDegrees(0)},
            {Axis.XP.rotationDegrees(90), Axis.YP.rotationDegrees(20), Axis.ZP.rotationDegrees(20)},
            {Axis.XP.rotationDegrees(90), Axis.YP.rotationDegrees(-20), Axis.ZP.rotationDegrees(20)},
            {Axis.XP.rotationDegrees(90), Axis.YP.rotationDegrees(10), Axis.ZP.rotationDegrees(45)},
            {Axis.XP.rotationDegrees(90), Axis.YP.rotationDegrees(-10), Axis.ZP.rotationDegrees(-45)},
            {Axis.XP.rotationDegrees(90), Axis.YP.rotationDegrees(10), Axis.ZP.rotationDegrees(35)},
            {Axis.XP.rotationDegrees(90), Axis.YP.rotationDegrees(10), Axis.ZP.rotationDegrees(10)},
            {Axis.XP.rotationDegrees(90), Axis.YP.rotationDegrees(10), Axis.ZP.rotationDegrees(-35)},
            {Axis.XP.rotationDegrees(90), Axis.YP.rotationDegrees(10), Axis.ZP.rotationDegrees(75)},
            {Axis.XP.rotationDegrees(90), Axis.YP.rotationDegrees(10), Axis.ZP.rotationDegrees(-75)},
            {Axis.XP.rotationDegrees(90), Axis.YP.rotationDegrees(10), Axis.ZP.rotationDegrees(-35)},
            {Axis.XP.rotationDegrees(90), Axis.YP.rotationDegrees(20), Axis.ZP.rotationDegrees(-10)},
            {Axis.XP.rotationDegrees(90), Axis.YP.rotationDegrees(10), Axis.ZP.rotationDegrees(75)},
            {Axis.XP.rotationDegrees(90), Axis.YP.rotationDegrees(20), Axis.ZP.rotationDegrees(-35)},
            {Axis.XP.rotationDegrees(90), Axis.YP.rotationDegrees(20), Axis.ZP.rotationDegrees(-10)},
            {Axis.XP.rotationDegrees(90), Axis.YP.rotationDegrees(-25), Axis.ZP.rotationDegrees(-75)},
            {Axis.XP.rotationDegrees(90), Axis.YP.rotationDegrees(20), Axis.ZP.rotationDegrees(35)},
            {Axis.XP.rotationDegrees(90), Axis.YP.rotationDegrees(-25), Axis.ZP.rotationDegrees(-75)},
    };

    private final BlockEntityRendererProvider.Context context;
    public SellingBinBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(@NotNull SellingBinBlockEntity sellingBinBlockEntity, float v, @NotNull PoseStack stack, @NotNull MultiBufferSource buffer, int combinedOverlay, int packedLight) {
        ItemRenderer itemRenderer = this.context.getItemRenderer();

        ArrayList<Item> items = sellingBinBlockEntity.getRenderStacks();

        int total = sellingBinBlockEntity.getTotalItems();
        int toRender = Math.min(total, RENDER_STACKS.length);

        ArrayList<Integer> idxs = new ArrayList<>(toRender);
        for (int i = 0; i < toRender; i++) {
            idxs.add(i);
        }

        Collections.shuffle(idxs);

        if (!items.isEmpty()) {
            for (int i : idxs) {
                ItemStack item = new ItemStack(items.get(i % items.size()));

                Vec3 pos = RENDER_STACKS[i];
                stack.pushPose();
                stack.translate(pos.x, pos.y, pos.z);
                stack.mulPose(RENDER_ROTS[i][0]);
                stack.mulPose(RENDER_ROTS[i][1]);
                stack.mulPose(RENDER_ROTS[i][2]);
                stack.scale(0.6f, 0.6f, 0.6f);

                itemRenderer.renderStatic(item, ItemDisplayContext.FIXED, getLightLevel(sellingBinBlockEntity.getLevel(), sellingBinBlockEntity.getBlockPos()),
                        OverlayTexture.NO_OVERLAY, stack, buffer, sellingBinBlockEntity.getLevel(), 1);

                stack.popPose();
            }
        }
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int blight =  level.getBrightness(LightLayer.BLOCK, pos);
        int slight =  level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(blight, slight);
    }
}
