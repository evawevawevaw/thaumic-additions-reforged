package thaumicadditions.damage;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public final class ModDamageTypes {
    public static final ResourceKey<DamageType> INVALID_RELIC_OWNER = ResourceKey.create(Registries.DAMAGE_TYPE,
        ResourceLocation.parse("thaumicadditions:invalid_relic_owner"));
    public static final ResourceKey<DamageType> RELIC_NONPLAYER = ResourceKey.create(Registries.DAMAGE_TYPE,
        ResourceLocation.parse("thaumicadditions:relic_nonplayer"));

    public static final ResourceKey<DamageType> SINGULARITY = ResourceKey.create(Registries.DAMAGE_TYPE,
        ResourceLocation.parse("thaumicadditions:singularity"));
    public static final ResourceKey<DamageType> SINGULARITY_BOSS = ResourceKey.create(Registries.DAMAGE_TYPE,
        ResourceLocation.parse("thaumicadditions:singularity_boss"));

    private ModDamageTypes() {
    }
}
