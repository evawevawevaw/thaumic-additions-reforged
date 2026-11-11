package thaumicadditions.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.client.platform.ItemGeometry;
import thaumicadditions.blockentity.InfuserBlockEntity;

public final class InfuserRenderer implements BlockEntityRenderer<InfuserBlockEntity> {
    public InfuserRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(InfuserBlockEntity machine, float partialTick, PoseStack pose, MultiBufferSource buffers,
        int light, int overlay) {
        ResourceLocation texture = ResourceLocation.parse("thaumicadditions:textures/model/" +
            (machine.dark() ? "darkinfusersymbol" : "infusersymbol") + ".png");
        boolean active = machine.working();
        int tint = active ? 0x30FFFFFF : machine.dark() ? 0x30555555 : 0x30808080;
        pose.pushPose();
        pose.translate(0.5, 0.93775, 0.5);
        if (active && machine.getLevel() != null) {
            pose.mulPose(Axis.YP.rotationDegrees(((machine.getLevel().getGameTime() + partialTick) % 72) * 5));
        }
        pose.scale(0.9F, 1, 0.9F);
        ItemGeometry.solid(buffers, pose, texture, true, sink -> {
            sink.vertex(-0.5F, 0, -0.5F, 0, 0, tint, 0xF000F0);
            sink.vertex(-0.5F, 0, 0.5F, 0, 1, tint, 0xF000F0);
            sink.vertex(0.5F, 0, 0.5F, 1, 1, tint, 0xF000F0);
            sink.vertex(0.5F, 0, -0.5F, 1, 0, tint, 0xF000F0);
        });
        pose.popPose();
    }
}
