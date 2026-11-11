package thaumicadditions.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import thaumicadditions.blockentity.CrystallizerBlockEntity;

public final class CrystallizerRenderer implements BlockEntityRenderer<CrystallizerBlockEntity> {
    private static final float[][] OFFSETS = {{0, 0}, {0.22F, 0.17F}, {-0.22F, 0.17F}, {-0.22F, -0.17F},
        {0.22F, -0.17F}, {0, -0.2F}, {0, 0.2F}};
    private static final int[] COLORS = {0xEEEEEE, 0xFFFF7E, 0xFF3C01, 0x0090FF, 0x00A000, 0xEECCFF, 0x555577};
    private static final float[] TILTS = {0, 0.3F, 0.2F, 0, 0, 0, 0};

    public CrystallizerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(CrystallizerBlockEntity machine, float partialTick, PoseStack pose, MultiBufferSource buffers,
        int light, int overlay) {
        if (machine.getLevel() == null) {
            return;
        }
        float rotation = ((machine.getLevel().getGameTime() + partialTick) % 720) / 2;
        float triangle = rotation < 180 ? rotation : 360 - rotation;
        for (int slot = 0; slot < 7; slot++) {
            boolean active = machine.hasCrystal(slot);
            double y = slot == 0 ? (active ? 1.3 + triangle / 720 : 1.05) : (active ? 1.05 + triangle / 720 : 1.05);
            float angle = active ? rotation : restingAngle(slot);
            MachineCrystal.render(pose, buffers, 0.5 + OFFSETS[slot][0], y, 0.5 + OFFSETS[slot][1],
                angle, TILTS[slot], COLORS[slot], 0xF000F0);
        }
    }

    static void renderItem(PoseStack pose, MultiBufferSource buffers, int light) {
        for (int slot = 0; slot < OFFSETS.length; slot++) {
            MachineCrystal.render(pose, buffers, 0.5 + OFFSETS[slot][0], 1.05, 0.5 + OFFSETS[slot][1],
                restingAngle(slot), TILTS[slot], COLORS[slot], 0xF000F0);
        }
    }

    private static float restingAngle(int slot) {
        return slot * 37;
    }

    @Override
    public boolean shouldRenderOffScreen(CrystallizerBlockEntity machine) {
        return true;
    }
}
