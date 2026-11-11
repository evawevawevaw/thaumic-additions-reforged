package thaumicadditions.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import thaumcraft.entity.projectile.PrimalOrb;
import thaumcraft.registry.ModBlocks;
import thaumcraft.registry.ModEffects;
import thaumcraft.registry.ModSounds;

public final class FocusProjectile extends PrimalOrb {
    private static final EntityDataAccessor<Boolean> INFECTION = SynchedEntityData.defineId(FocusProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> PURITY = SynchedEntityData.defineId(FocusProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> EXPLOSIVE = SynchedEntityData.defineId(FocusProjectile.class, EntityDataSerializers.INT);

    public FocusProjectile(EntityType<? extends FocusProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(INFECTION, false);
        builder.define(PURITY, false);
        builder.define(EXPLOSIVE, 0);
    }

    public void configure(boolean infection, boolean purity, int explosive) {
        entityData.set(INFECTION, infection);
        entityData.set(PURITY, purity && !infection);
        entityData.set(EXPLOSIVE, Math.clamp(explosive, 0, 5));
    }

    @Override
    protected void onHit(HitResult hit) {
        if (!(level() instanceof ServerLevel server)) {
            return;
        }
        server.playSound(null, blockPosition(), ModSounds.ZAP.get(), SoundSource.PLAYERS, 1, 1);
        int power = entityData.get(EXPLOSIVE);
        if (power > 0) {
            server.explode(this, getX(), getY(), getZ(), power * 4.0F, Level.ExplosionInteraction.TNT);
        }
        if (hit instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof LivingEntity living) {
            if (!entityData.get(INFECTION)
                || !(living instanceof Player target && getOwner() instanceof Player owner && !owner.canHarmPlayer(target))) {
                MobEffectInstance effect = entityData.get(INFECTION) ? new MobEffectInstance(ModEffects.TAINT_POISON, 600, 2)
                    : entityData.get(PURITY) ? new MobEffectInstance(MobEffects.REGENERATION, 600, 2) : null;
                if (effect != null) {
                    effect.getCures().clear();
                    living.addEffect(effect, getOwner());
                }
                if (entityData.get(PURITY) && living instanceof Animal animal && animal.canFallInLove()) {
                    animal.setInLove(getOwner() instanceof Player player ? player : null);
                }
            }
        } else if (hit instanceof BlockHitResult blockHit) {
            changeFlux(server, blockHit.getBlockPos());
        }
        discard();
    }

    private boolean allowed(ServerLevel level, BlockPos pos) {
        return level.hasChunk(pos.getX() >> 4, pos.getZ() >> 4) && !level.isOutsideBuildHeight(pos)
            && (!(getOwner() instanceof Player player) || level.mayInteract(player, pos)
                && player.mayUseItemAt(pos, Direction.UP, player.getMainHandItem()));
    }

    private void changeFlux(ServerLevel level, BlockPos center) {
        boolean infection = entityData.get(INFECTION);
        if (!infection && !entityData.get(PURITY)) {
            return;
        }
        int radius = infection ? 3 : 8;
        int placed = 0;
        int upper = infection ? radius : radius - 1;
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, -radius, -radius), center.offset(upper, upper, upper))) {
            if (!allowed(level, pos)) {
                continue;
            }
            BlockState state = level.getBlockState(pos);
            if (infection && state.isAir() && random.nextInt(100) < 25) {
                BlockSnapshot snapshot = BlockSnapshot.create(level.dimension(), level, pos);
                var flux = random.nextBoolean() ? ModBlocks.FLUX_GAS.get() : ModBlocks.FLUX_GOO.get();
                level.setBlock(pos, flux.defaultBlockState(), 3);
                if (EventHooks.onBlockPlace(getOwner() == null ? this : getOwner(), snapshot, Direction.UP)) {
                    snapshot.restore(3);
                } else if (++placed >= 64) {
                    return;
                }
            } else if (!infection && (state.is(ModBlocks.FLUX_GAS.get()) || state.is(ModBlocks.FLUX_GOO.get()))
                && random.nextInt(100) < 75) {
                if (!(getOwner() instanceof ServerPlayer player)
                    || !NeoForge.EVENT_BUS.post(new BlockEvent.BreakEvent(level, pos, state, player)).isCanceled()) {
                    level.removeBlock(pos, false);
                }
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("infection", entityData.get(INFECTION));
        tag.putBoolean("purity", entityData.get(PURITY));
        tag.putInt("explosive", entityData.get(EXPLOSIVE));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        configure(tag.getBoolean("infection"), tag.getBoolean("purity"), tag.getInt("explosive"));
    }
}
