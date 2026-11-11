package thaumicadditions.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.client.render.NodeRendering;
import thaumcraft.node.NodeModifier;
import thaumcraft.node.NodeType;
import thaumicadditions.blockentity.CreativeNodeBlockEntity;

public final class CreativeNodeRenderer implements BlockEntityRenderer<CreativeNodeBlockEntity> {
    public CreativeNodeRenderer(BlockEntityRendererProvider.Context context) {
    }

    static AspectList visualAspects() {
        AspectList aspects = new AspectList();
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            aspects.add(aspect, 40);
        }
        return aspects;
    }

    @Override
    public void render(CreativeNodeBlockEntity node, float partialTick, PoseStack pose, MultiBufferSource buffers,
        int light, int overlay) {
        if (node.getLevel() == null) {
            return;
        }
        pose.pushPose();
        pose.translate(0.5, 0.5, 0.5);
        pose.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        float ticks = (node.getLevel().getGameTime() % 100000) + partialTick;
        NodeRendering.submit(buffers, pose, visualAspects(), NodeType.PURE, NodeModifier.BRIGHT, ticks);
        pose.popPose();
    }
}
