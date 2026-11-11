package thaumicadditions.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.client.platform.ItemGeometry;
import thaumicadditions.blockentity.TileBoosterBlockEntity;

public final class TileBoosterRenderer implements BlockEntityRenderer<TileBoosterBlockEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("thaumicadditions:textures/block/tile_booster.png");

    public TileBoosterRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TileBoosterBlockEntity machine, float partialTick, PoseStack pose, MultiBufferSource buffers,
        int light, int overlay) {
        if (machine.getLevel() == null) {
            return;
        }
        pose.pushPose();
        pose.translate(0.5, 0.5, 0.5);
        pose.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        pose.mulPose(Axis.ZP.rotationDegrees(((machine.getLevel().getGameTime() % 720) + partialTick) * 0.5F));
        ItemGeometry.solid(buffers, pose, TEXTURE, true, sink -> {
            sink.vertex(-0.5F, -0.5F, 0, 0, 1, -1, 0xF000F0);
            sink.vertex(0.5F, -0.5F, 0, 1, 1, -1, 0xF000F0);
            sink.vertex(0.5F, 0.5F, 0, 1, 0, -1, 0xF000F0);
            sink.vertex(-0.5F, 0.5F, 0, 0, 0, -1, 0xF000F0);
        });
        pose.popPose();
    }
}
