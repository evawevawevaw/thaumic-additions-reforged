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
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import thaumicadditions.blockentity.ThaumicGeneratorBlockEntity;

public final class ThaumicGeneratorRenderer implements BlockEntityRenderer<ThaumicGeneratorBlockEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(ResourceLocation.parse("thaumicadditions:thaumic_generator"), "main");
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("thaumicadditions:textures/model/thaumic_generator.png");
    private final ModelPart model;

    public ThaumicGeneratorRenderer(BlockEntityRendererProvider.Context context) {
        model = context.bakeLayer(LAYER);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        var root = mesh.getRoot();
        root.addOrReplaceChild("up", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0, 0, 0, 8, 2, 8), PartPose.offset(-4, 8, -4));
        root.addOrReplaceChild("east", CubeListBuilder.create().texOffs(0, 10).mirror().addBox(0, 0, 0, 2, 8, 8), PartPose.offset(-8, 12, -4));
        root.addOrReplaceChild("west", CubeListBuilder.create().texOffs(0, 10).mirror().addBox(0, 0, 0, 2, 8, 8), PartPose.offset(6, 12, -4));
        root.addOrReplaceChild("down", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0, 0, 0, 8, 2, 8), PartPose.offset(-4, 22, -4));
        root.addOrReplaceChild("north", CubeListBuilder.create().texOffs(0, 26).mirror().addBox(0, 0, 0, 8, 8, 2), PartPose.offset(-4, 12, -8));
        root.addOrReplaceChild("south", CubeListBuilder.create().texOffs(0, 26).mirror().addBox(0, 0, 0, 8, 8, 2), PartPose.offset(-4, 12, 6));
        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    public void render(ThaumicGeneratorBlockEntity machine, float partialTick, PoseStack pose, MultiBufferSource buffers,
        int light, int overlay) {
        renderItem(pose, buffers, light, overlay);
    }

    void renderItem(PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        for (Direction side : Direction.values()) {
            model.getChild(side.getName()).visible = true;
        }
        renderModel(pose, buffers, light, overlay);
    }

    private void renderModel(PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        pose.pushPose();
        pose.translate(0.5, 1.5, 0.5);
        pose.mulPose(Axis.ZP.rotationDegrees(180));
        model.render(pose, buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), light, overlay);
        pose.popPose();
    }
}
