package thaumicadditions.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import thaumicadditions.blockentity.InfusionActivatorBlockEntity;

public final class InfusionActivatorRenderer implements BlockEntityRenderer<InfusionActivatorBlockEntity> {
    public InfusionActivatorRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(InfusionActivatorBlockEntity machine, float partialTick, PoseStack pose, MultiBufferSource buffers,
        int light, int overlay) {
        if (!machine.hasWand()) {
            return;
        }
        pose.pushPose();
        pose.translate(0.5, 0.8 + machine.phase(partialTick) / 720, 0.5);
        pose.mulPose(Axis.ZP.rotationDegrees(180));
        pose.scale(0.5F, 0.5F, 0.5F);
        Minecraft.getInstance().getItemRenderer().renderStatic(machine.displayedWand(), ItemDisplayContext.GROUND,
            light, overlay, pose, buffers, machine.getLevel(), 0);
        pose.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(InfusionActivatorBlockEntity machine) {
        return true;
    }
}
