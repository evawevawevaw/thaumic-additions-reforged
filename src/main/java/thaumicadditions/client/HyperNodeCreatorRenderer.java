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
import thaumicadditions.blockentity.HyperNodeCreatorBlockEntity;

public final class HyperNodeCreatorRenderer implements BlockEntityRenderer<HyperNodeCreatorBlockEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(ResourceLocation.parse("thaumicadditions:hyper_node_creator"), "main");
    private final ModelPart model;

    public HyperNodeCreatorRenderer(BlockEntityRendererProvider.Context context) {
        model = context.bakeLayer(LAYER);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        var root = mesh.getRoot();
        root.addOrReplaceChild("shape1", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0.0F, 0.0F, 0.0F, 16, 8, 16), PartPose.offset(-8.0F, 16.0F, -8.0F));
        root.addOrReplaceChild("shape2", CubeListBuilder.create().texOffs(46, 25).mirror().addBox(0.0F, 0.0F, 0.0F, 4, 6, 4), PartPose.offset(-2.0F, 10.0F, -2.0F));
        root.addOrReplaceChild("shape3", CubeListBuilder.create().texOffs(20, 25).mirror().addBox(0.0F, 0.0F, 0.0F, 6, 1, 6), PartPose.offset(-3.0F, 9.0F, -3.0F));
        root.addOrReplaceChild("shape4", CubeListBuilder.create().texOffs(0, 40).mirror().addBox(0.0F, 0.0F, 0.0F, 16, 2, 16), PartPose.offset(-8.0F, 7.0F, -8.0F));
        root.addOrReplaceChild("shape5", CubeListBuilder.create().texOffs(66, 0).mirror().addBox(0.0F, 0.0F, 0.0F, 1, 15, 1), PartPose.offset(-8.0F, -8.0F, -8.0F));
        root.addOrReplaceChild("shape6", CubeListBuilder.create().texOffs(66, 0).mirror().addBox(0.0F, 0.0F, 0.0F, 1, 15, 1), PartPose.offset(7.0F, -8.0F, 7.0F));
        root.addOrReplaceChild("shape7", CubeListBuilder.create().texOffs(66, 0).mirror().addBox(0.0F, 0.0F, 0.0F, 1, 15, 1), PartPose.offset(7.0F, -8.0F, -8.0F));
        root.addOrReplaceChild("shape8", CubeListBuilder.create().texOffs(66, 0).mirror().addBox(0.0F, 0.0F, 0.0F, 1, 15, 1), PartPose.offset(-8.0F, -8.0F, 7.0F));
        return LayerDefinition.create(mesh, 256, 128);
    }

    @Override
    public void render(HyperNodeCreatorBlockEntity machine, float partialTick, PoseStack pose, MultiBufferSource buffers,
        int light, int overlay) {
        for (int part = 4; part <= 8; part++) {
            model.getChild("shape" + part).visible = machine.nodePresent();
        }
        ResourceLocation texture = ResourceLocation.parse("thaumicadditions:textures/model/hyper_node_creator" +
            (machine.primalsOnly() ? "_primals" : "") + ".png");
        pose.pushPose();
        pose.translate(0.5, 1.5, 0.5);
        pose.mulPose(Axis.ZP.rotationDegrees(180));
        model.render(pose, buffers.getBuffer(RenderType.entityCutoutNoCull(texture)), light, overlay);
        pose.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(HyperNodeCreatorBlockEntity machine) {
        return true;
    }
}
