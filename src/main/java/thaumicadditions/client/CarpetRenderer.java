package thaumicadditions.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import thaumicadditions.entity.CarpetEntity;

public final class CarpetRenderer extends EntityRenderer<CarpetEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(ResourceLocation.parse("thaumicadditions:carpet"), "main");
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("thaumicadditions:textures/entity/carpet.png");
    private final ModelPart model;

    public CarpetRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = context.bakeLayer(LAYER);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        var root = mesh.getRoot();
        for (int segment = 0; segment < 7; segment++) {
            root.addOrReplaceChild("section" + segment, CubeListBuilder.create().texOffs(0, segment * 10).mirror()
                .addBox(-20, 0, 0, 40, 2, 8), PartPose.offset(0, 0, 20 - segment * 8));
        }
        for (int corner = 0; corner < 4; corner++) {
            root.addOrReplaceChild("tassel" + corner, CubeListBuilder.create().texOffs(0, 70).mirror()
                .addBox(0, 0, 0, 2, 2, 2), PartPose.offsetAndRotation(corner % 2 == 0 ? 20 : -20, 1,
                    corner < 2 ? -28 : 28, -0.7071F, 0, 0.7071F));
        }
        return LayerDefinition.create(mesh, 256, 128);
    }

    @Override
    public void render(CarpetEntity carpet, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) {
        pose.pushPose();
        pose.translate(0, 0.1, 0);
        pose.mulPose(Axis.YP.rotationDegrees(90 - carpet.getYHeadRot()));
        pose.scale(-0.575F, -0.25F, 0.575F);
        float time = (Minecraft.getInstance().player == null ? carpet.tickCount : Minecraft.getInstance().player.tickCount) + partialTick;
        for (int segment = 0; segment < 7; segment++) {
            pose.pushPose();
            pose.translate(0, Mth.sin((time + segment) / 4) * 0.25F + 0.25F, 0);
            model.getChild("section" + segment).render(pose, buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), light, OverlayTexture.NO_OVERLAY);
            if (segment == 0 || segment == 6) {
                pose.translate(0, -0.25, 0);
                pose.scale(1, 5, 1);
                int first = segment == 0 ? 2 : 0;
                model.getChild("tassel" + first).render(pose, buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), light, OverlayTexture.NO_OVERLAY);
                model.getChild("tassel" + (first + 1)).render(pose, buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), light, OverlayTexture.NO_OVERLAY);
            }
            pose.popPose();
        }
        pose.popPose();
        super.render(carpet, yaw, partialTick, pose, buffers, light);
    }

    @Override
    public ResourceLocation getTextureLocation(CarpetEntity entity) {
        return TEXTURE;
    }
}
