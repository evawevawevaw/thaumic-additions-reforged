package thaumicadditions.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumicadditions.ThaumicAdditions;
import thaumicadditions.entity.SingularityEntity;

public final class ModEntities {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, ThaumicAdditions.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<SingularityEntity>> SINGULARITY = ENTITIES.register("singularity",
        () -> EntityType.Builder.<SingularityEntity>of(SingularityEntity::new, MobCategory.MISC).sized(0.25F, 0.25F)
            .clientTrackingRange(4).updateInterval(3).build("thaumicadditions:singularity"));

    public static final DeferredHolder<EntityType<?>, EntityType<thaumicadditions.entity.CarpetEntity>> CARPET = ENTITIES.register("carpet",
        () -> EntityType.Builder.<thaumicadditions.entity.CarpetEntity>of(thaumicadditions.entity.CarpetEntity::new, MobCategory.MISC)
            .sized(0.6F, 1.8F).clientTrackingRange(4).updateInterval(3).build("thaumicadditions:carpet"));

    public static final DeferredHolder<EntityType<?>, EntityType<thaumicadditions.entity.CrueltyProbe>> CRUELTY_PROBE =
        ENTITIES.register("cruelty_probe", () -> EntityType.Builder.<thaumicadditions.entity.CrueltyProbe>of(
            thaumicadditions.entity.CrueltyProbe::new, MobCategory.MISC).sized(0.25F, 0.25F)
            .clientTrackingRange(1).updateInterval(20).build("thaumicadditions:cruelty_probe"));
    public static final DeferredHolder<EntityType<?>, EntityType<thaumicadditions.entity.FocusProjectile>> FOCUS_PROJECTILE =
        ENTITIES.register("focus_projectile", () -> EntityType.Builder.<thaumicadditions.entity.FocusProjectile>of(
            thaumicadditions.entity.FocusProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F)
            .clientTrackingRange(4).updateInterval(3).build("thaumicadditions:focus_projectile"));

    private ModEntities() {
    }

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }
}
