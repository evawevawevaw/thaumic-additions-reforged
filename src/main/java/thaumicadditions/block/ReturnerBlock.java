package thaumicadditions.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import thaumicadditions.blockentity.ReturnerBlockEntity;
import thaumicadditions.registry.ModDataComponents;

public final class ReturnerBlock extends Block implements EntityBlock {
    public ReturnerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ReturnerBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (level.getBlockEntity(pos) instanceof ReturnerBlockEntity returner) {
            GlobalPos target = stack.get(ModDataComponents.RETURN_DESTINATION.get());
            returner.setDestination(target == null ? GlobalPos.of(level.dimension(), pos) : target);
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, net.minecraft.world.phys.HitResult hit, LevelReader level, BlockPos pos, Player player) {
        ItemStack result = new ItemStack(this);
        if (level instanceof Level world) {
            result.set(ModDataComponents.RETURN_DESTINATION.get(), GlobalPos.of(world.dimension(), pos));
        }
        return result;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        if (player instanceof ServerPlayer server && level.getBlockEntity(pos) instanceof ReturnerBlockEntity returner) {
            GlobalPos destination = returner.destination();
            var target = server.server.getLevel(destination.dimension());
            BlockPos arrival = destination.pos();
            if (target == null || !target.getWorldBorder().isWithinBounds(arrival) || target.isOutsideBuildHeight(arrival)) {
                return InteractionResult.FAIL;
            }
            var shape = target.getBlockState(arrival).getCollisionShape(target, arrival);
            double y = arrival.getY() + (shape.isEmpty() ? 0 : shape.max(net.minecraft.core.Direction.Axis.Y));
            Vec3 position = new Vec3(arrival.getX() + 0.5, y, arrival.getZ() + 0.5);
            if (!target.noCollision(server, server.getBoundingBox().move(position.subtract(server.position())))) {
                return InteractionResult.FAIL;
            }
            server.teleportTo(target, position.x, position.y, position.z, server.getYRot(), server.getXRot());
            server.setDeltaMovement(Vec3.ZERO);
            server.resetFallDistance();
            server.setPortalCooldown(300000);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        for (int particle = 0; particle < 10; particle++) {
            double x = random.nextInt(3) - random.nextInt(3);
            double y = random.nextInt(3);
            double z = random.nextInt(3) - random.nextInt(3);
            level.addParticle(ParticleTypes.PORTAL, pos.getX() + x + 0.5, pos.getY() + y + 0.5, pos.getZ() + z + 0.5, x, y, z);
        }
    }
}
