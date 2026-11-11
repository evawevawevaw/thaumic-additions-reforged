package thaumicadditions.entity;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import thaumcraft.entity.CombatUtils;
import thaumcraft.entity.projectile.AlumentumProjectile;
import thaumicadditions.damage.ModDamageTypes;
import thaumicadditions.fx.SingularityEffects;
import thaumicadditions.registry.ModEntities;
import thaumicadditions.registry.ModItems;
import thaumicadditions.registry.ModSounds;

public final class SingularityEntity extends AlumentumProjectile {
    private static final EntityDataAccessor<Boolean> ANCHORED = SynchedEntityData.defineId(SingularityEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> ENERGY = SynchedEntityData.defineId(SingularityEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> AGE = SynchedEntityData.defineId(SingularityEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> RADIUS = SynchedEntityData.defineId(SingularityEntity.class, EntityDataSerializers.INT);

    public SingularityEntity(EntityType<? extends SingularityEntity> type, Level level) {
        super(type, level);
    }

    public static SingularityEntity thrown(Level level, LivingEntity owner) {
        SingularityEntity entity = new SingularityEntity(ModEntities.SINGULARITY.get(), level);
        CombatUtils.launch(entity, owner, 0.75F);
        return entity;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ANCHORED, false);
        builder.define(ENERGY, 0.0F);
        builder.define(AGE, 0);
        builder.define(RADIUS, 10);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.SINGULARITY.get();
    }

    public boolean anchored() {
        return entityData.get(ANCHORED);
    }

    public float energy() {
        return entityData.get(ENERGY);
    }

    public int activeAge() {
        return entityData.get(AGE);
    }

    public int radius() {
        return entityData.get(RADIUS);
    }

    public double lift() {
        return Math.min(activeAge() / 400.0, 1) * 4;
    }

    private void anchor() {
        entityData.set(ANCHORED, true);
        setNoGravity(true);
        setDeltaMovement(Vec3.ZERO);
    }

    @Override
    protected void onHit(HitResult hit) {
        if (!level().isClientSide) {
            anchor();
            level().playSound(null, getX(), getY(), getZ(), ModSounds.SINGULARITY.get(), SoundSource.NEUTRAL, 16, 1);
        }
    }

    @Override
    public void tick() {
        if (getY() < level().getMinBuildHeight()) {
            if (!level().isClientSide) {
                playSound(thaumcraft.registry.ModSounds.CRAFTFAIL.get(), 1, 1);
                discard();
            }
            return;
        }
        if (!anchored()) {
            super.tick();
            return;
        }
        setDeltaMovement(Vec3.ZERO);
        if (level().isClientSide) {
            SingularityEffects.emit(this);
            return;
        }
        ServerLevel server = (ServerLevel)level();
        entityData.set(AGE, activeAge() == Integer.MAX_VALUE ? Integer.MAX_VALUE : activeAge() + 1);
        if (activeAge() >= 2000 || energy() >= 2000) {
            for (int blast = 0; blast < 5; blast++) {
                server.explode(this, getX(), getY(), getZ(), energy() / 200, Level.ExplosionInteraction.BLOCK);
            }
            discard();
            return;
        }
        boolean griefing = EventHooks.canEntityGrief(server, this);
        if (griefing) {
            erode(server);
        }
        AABB area = new AABB(getX() - radius(), getY() - radius(), getZ() - radius(),
            getX() + radius(), getY() + radius(), getZ() + radius());
        for (AlumentumProjectile projectile : server.getEntitiesOfClass(AlumentumProjectile.class, area,
            entity -> entity.isAlive() && !(entity instanceof SingularityEntity))) {
            Vec3 position = projectile.position();
            projectile.discard();
            server.explode(this, position.x, position.y, position.z, 4,
                griefing ? Level.ExplosionInteraction.BLOCK : Level.ExplosionInteraction.NONE);
            SingularityEntity child = new SingularityEntity(ModEntities.SINGULARITY.get(), server);
            child.setPos(position);
            child.setOwner(getOwner());
            child.anchor();
            server.addFreshEntity(child);
        }
        Vec3 center = position().add(0, lift(), 0);
        for (Entity entity : server.getEntities(this, area, entity -> entity.isAlive() && !entity.isSpectator())) {
            entity.setDeltaMovement(center.subtract(entity.position()).scale(0.1));
            entity.hurtMarked = true;
        }
        float before = energy();
        AABB core = new AABB(center.subtract(1, 1, 1), center.add(1, 1, 1));
        List<Entity> captured = server.getEntities(this, core, entity -> entity.isAlive() && !entity.isSpectator());
        for (Entity entity : captured) {
            if (!entity.isAlive()) {
                continue;
            }
            if (entity instanceof ItemEntity item) {
                item.discard();
                entityData.set(ENERGY, energy() + 1);
            } else if (entity instanceof SingularityEntity other) {
                entityData.set(ENERGY, energy() + other.energy());
                entityData.set(RADIUS, (int)Math.min(Integer.MAX_VALUE, (long)radius() + other.radius()));
                entityData.set(AGE, (int)Math.min(Integer.MAX_VALUE, (long)activeAge() + other.activeAge()));
                other.discard();
                playSound(ModSounds.SINGULARITY.get(), 16, 1);
            } else if (entity instanceof LivingEntity living) {
                if (living instanceof Player target && getOwner() instanceof Player owner && !owner.canHarmPlayer(target)) {
                    continue;
                }
                boolean boss = living instanceof EnderDragon || living instanceof WitherBoss;
                DamageSource damage = server.damageSources().source(boss ? ModDamageTypes.SINGULARITY_BOSS : ModDamageTypes.SINGULARITY,
                    this, getOwner());
                living.hurt(damage, 800000);
            }
        }
        if (energy() - before >= 20) {
            entityData.set(RADIUS, radius() == Integer.MAX_VALUE ? Integer.MAX_VALUE : radius() + 1);
        }
    }

    private void erode(ServerLevel level) {
        for (int attempt = 0; attempt < 128; attempt++) {
            BlockPos pos = new BlockPos((int)getX() + random.nextInt(radius()) - random.nextInt(radius()),
                (int)getY() + random.nextInt(radius()) - random.nextInt(radius()),
                (int)getZ() + random.nextInt(radius()) - random.nextInt(radius()));
            if (!level.isLoaded(pos) || !level.isInWorldBounds(pos)) {
                continue;
            }
            BlockState state = level.getBlockState(pos);
            if (!state.isAir() && state.getDestroySpeed(level, pos) >= 0) {
                var sound = state.getSoundType(level, pos, this);
                level.playSound(null, pos, sound.getBreakSound(), SoundSource.BLOCKS, sound.getVolume(), sound.getPitch());
                level.destroyBlock(pos, true, this);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Anchored", anchored());
        tag.putFloat("Energy", energy());
        tag.putInt("ActiveAge", activeAge());
        tag.putInt("Radius", radius());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        float energy = tag.getFloat("Energy");
        entityData.set(ENERGY, Float.isFinite(energy) ? Math.max(0, energy) : 0);
        entityData.set(AGE, Math.max(0, tag.getInt("ActiveAge")));
        entityData.set(RADIUS, tag.contains("Radius") ? Math.max(1, tag.getInt("Radius")) : 10);
        if (tag.getBoolean("Anchored")) {
            anchor();
        }
    }
}
