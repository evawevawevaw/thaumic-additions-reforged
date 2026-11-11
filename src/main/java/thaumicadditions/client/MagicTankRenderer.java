package thaumicadditions.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import thaumicadditions.blockentity.MagicTankBlockEntity;
import thaumicadditions.magic.MagicKind;

public final class MagicTankRenderer implements BlockEntityRenderer<MagicTankBlockEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
        ResourceLocation.parse("thaumicadditions:magic_tank_liquid"), "main");
    private final ModelPart liquid;

    public MagicTankRenderer(BlockEntityRendererProvider.Context context) {
        liquid = context.bakeLayer(LAYER);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        mesh.getRoot().addOrReplaceChild("liquid", CubeListBuilder.create().texOffs(4, 1)
            .addBox(0, 0, 0, 14, 16, 14), PartPose.offset(-7, 8, -7));
        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void render(MagicTankBlockEntity tank, float partialTick, PoseStack pose, MultiBufferSource buffers,
        int light, int overlay) {
        int total = tank.totalStored();
        if (total <= 0) {
            return;
        }
        long time = tank.getLevel() == null ? 0 : tank.getLevel().getGameTime();
        int frame = (int) ((time * 50L) % 1600L / 100L);
        ResourceLocation texture = ResourceLocation.parse("thaumicadditions:textures/model/tank_liquid/" + frame + ".png");
        int vis = MagicKind.VIS.color();
        int taint = MagicKind.TAINT.color();
        int red = ((vis >> 16 & 255) + (taint >> 16 & 255)) / 2;
        int green = ((vis >> 8 & 255) + (taint >> 8 & 255)) / 2;
        int blue = ((vis & 255) + (taint & 255)) / 2;
        int color = 0xFF000000 | red << 16 | green << 8 | blue;
        pose.pushPose();
        pose.translate(0.5, 0.001, 0.5);
        pose.mulPose(Axis.ZP.rotationDegrees(180));
        pose.scale(0.99F, Math.max(0.001F, total / (tank.capacity() + 5.0F) - 0.011F), 0.99F);
        pose.translate(0, -1.51, 0);
        liquid.render(pose, buffers.getBuffer(RenderType.entityTranslucent(texture)), light, overlay, color);
        pose.popPose();
    }
}
