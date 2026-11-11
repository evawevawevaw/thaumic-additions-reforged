package thaumicadditions.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.client.geom.ModelBoxGeometry;
import thaumcraft.client.platform.ItemGeometry;

final class MachineCrystal {
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("thaumcraft:textures/model/crystal.png");

    private MachineCrystal() {
    }

    static void render(PoseStack pose, MultiBufferSource buffers, double x, double y, double z,
        float rotation, float tilt, int color, int light) {
        int red = Math.min(255, Math.round((color >> 16 & 255) * 255.0F / 220));
        int green = Math.min(255, Math.round((color >> 8 & 255) * 255.0F / 220));
        int blue = Math.min(255, Math.round((color & 255) * 255.0F / 220));
        int tint = 0xFF000000 | red << 16 | green << 8 | blue;
        pose.pushPose();
        pose.translate(x, y, z);
        pose.mulPose(Axis.XP.rotationDegrees(180));
        pose.mulPose(Axis.YP.rotationDegrees(rotation));
        pose.mulPose(Axis.XP.rotationDegrees(tilt));
        pose.scale(0.1575F, 0.52F, 0.1575F);
        pose.translate(0, 2, 0);
        pose.mulPose(Axis.ZP.rotation(0.7071F));
        pose.mulPose(Axis.XP.rotation(0.7071F));
        ItemGeometry.solid(buffers, pose, TEXTURE, true, sink ->
            ModelBoxGeometry.box(sink, -16, -16, 0, 16, 16, 16, 0, 0, 64, 32, true, tint, light));
        pose.popPose();
    }
}
