package thaumicadditions.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import thaumcraft.client.platform.EldritchSurfaces;
import thaumcraft.client.platform.ImmediateGeometry;
import thaumicadditions.blockentity.VoidChestBlockEntity;

public final class VoidChestRenderer implements BlockEntityRenderer<VoidChestBlockEntity> {
    public VoidChestRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(VoidChestBlockEntity chest, float partialTick, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        if (chest.getLevel() == null) {
            return;
        }
        int mask = 0;
        for (Direction direction : Direction.values()) {
            var neighborPos = chest.getBlockPos().relative(direction);
            if (!chest.getLevel().getBlockState(neighborPos).isSolidRender(chest.getLevel(), neighborPos)) {
                mask |= 1 << direction.ordinal();
            }
        }
        boolean nearby = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition()
            .distanceToSqr(Vec3.atCenterOf(chest.getBlockPos())) < 512;
        pose.pushPose();
        pose.translate(0.001, 0.001, 0.001);
        pose.scale(0.998F, 0.998F, 0.998F);
        EldritchSurfaces.submitPortalFaces(new ImmediateGeometry(buffers), pose, mask, nearby,
            (chest.getLevel().getGameTime() % 100000) + partialTick);
        pose.popPose();
    }
}
