package thaumicadditions.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import thaumicadditions.blockentity.CondenserBlockEntity;

public final class CondenserRenderer implements BlockEntityRenderer<CondenserBlockEntity> {
    public CondenserRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(CondenserBlockEntity machine, float partialTick, PoseStack pose, MultiBufferSource buffers,
        int light, int overlay) {
        if (machine.remaining() < 0 || machine.getLevel() == null) {
            return;
        }
        float rotation = ((machine.getLevel().getGameTime() + partialTick) % 720) / 2;
        float triangle = rotation < 180 ? rotation : 360 - rotation;
        MachineCrystal.render(pose, buffers, 0.5, 2.0 + triangle / 720, 0.5, rotation, 0, machine.crystalColor(), 0xF000F0);
    }

    @Override
    public boolean shouldRenderOffScreen(CondenserBlockEntity machine) {
        return true;
    }
}
