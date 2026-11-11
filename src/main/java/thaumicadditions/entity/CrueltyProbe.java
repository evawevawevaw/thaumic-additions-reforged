package thaumicadditions.entity;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public final class CrueltyProbe extends ThrowableProjectile {
    public CrueltyProbe(EntityType<? extends CrueltyProbe> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected double getDefaultGravity() {
        return 0;
    }

    private void affect(LivingEntity target) {
        if (target == getOwner() || target instanceof Player other && getOwner() instanceof Player owner
            && !owner.canHarmPlayer(other)) {
            return;
        }
        target.addEffect(new MobEffectInstance(MobEffects.WITHER, 1, 50), getOwner());
    }

    @Override
    protected void onHit(HitResult hit) {
        if (!level().isClientSide) {
            if (hit instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof LivingEntity living) {
                affect(living);
            }
            discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            for (Monster monster : level().getEntitiesOfClass(Monster.class, getBoundingBox(), LivingEntity::isAlive)) {
                affect(monster);
                monster.setDeltaMovement(Vec3.ZERO);
            }
            if (tickCount >= 10) {
                discard();
            }
        }
    }
}
