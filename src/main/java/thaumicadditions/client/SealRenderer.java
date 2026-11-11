package thaumicadditions.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.aspect.Aspect;
import thaumcraft.client.platform.ItemGeometry;
import thaumicadditions.block.SealBlock;
import thaumicadditions.blockentity.SealBlockEntity;

public final class SealRenderer implements BlockEntityRenderer<SealBlockEntity> {
    public SealRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(SealBlockEntity seal, float partialTick, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        pose.pushPose();
        pose.translate(0.5, 0.5, 0.5);
        pose.mulPose(seal.getBlockState().getValue(SealBlock.FACING).getRotation());
        pose.translate(0, -0.49, 0);
        layer(pose, buffers, "textures/model/seal.png", -1, light, 0.15F);
        pose.translate(0, 0.001, 0);
        layer(pose, buffers, "textures/model/seal_over.png", 0xFF000000 | seal.data().color(), light, 0.15F);
        for (int index = 0; index < seal.data().aspects().size(); index++) {
            Aspect aspect = Aspect.getAspect(seal.data().aspects().get(index));
            if (aspect != null) {
                pose.translate(0, 0.001, 0);
                layer(pose, buffers, "textures/block/seal_/s_" + index + ".png", 0xFF000000 | aspect.getColor(), 0xF000F0, 0.5F);
            }
        }
        pose.popPose();
    }

    private static void layer(PoseStack pose, MultiBufferSource buffers, String texture, int color, int light, float radius) {
        ItemGeometry.solid(buffers, pose, ResourceLocation.fromNamespaceAndPath("thaumicadditions", texture), true, sink -> {
            sink.vertex(-radius, 0, -radius, 16 / 512.0F, 20 / 256.0F, color, light);
            sink.vertex(-radius, 0, radius, 16 / 512.0F, 36 / 256.0F, color, light);
            sink.vertex(radius, 0, radius, 32 / 512.0F, 36 / 256.0F, color, light);
            sink.vertex(radius, 0, -radius, 32 / 512.0F, 20 / 256.0F, color, light);
        });
    }
}
