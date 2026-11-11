package thaumicadditions.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.util.DeferredSoundType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumicadditions.ThaumicAdditions;

public final class ModSounds {
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, ThaumicAdditions.MOD_ID);
    public static final DeferredHolder<SoundEvent, SoundEvent> DIG_GLASSWOOD = sound("dig.glasswood");
    public static final DeferredHolder<SoundEvent, SoundEvent> STEP_GLASSWOOD = sound("step.glasswood");
    public static final DeferredHolder<SoundEvent, SoundEvent> SINGULARITY = sound("singularity");
    public static final DeferredHolder<SoundEvent, SoundEvent> SEAL_SYMBOL_SET = sound("seal_symbol_set");
    public static final DeferredHolder<SoundEvent, SoundEvent> INFUSER = sound("infuser");
    public static final DeferredHolder<SoundEvent, SoundEvent> VOIDCHESTOPEN = sound("voidchestopen");
    public static final DeferredHolder<SoundEvent, SoundEvent> VOIDCHESTCLOSE = sound("voidchestclose");
    public static final DeferredHolder<SoundEvent, SoundEvent> PORTALOPEN = sound("portalopen");
    public static final DeferredHolder<SoundEvent, SoundEvent> PORTALCLOSE = sound("portalclose");
    public static final DeferredHolder<SoundEvent, SoundEvent> TINKERING = sound("tinkering");
    public static final DeferredHolder<SoundEvent, SoundEvent> BUBBLING = sound("bubbling");
    public static final DeferredHolder<SoundEvent, SoundEvent> HALLELUJAH = sound("hallelujah");
    public static final SoundType WOOD_GLASS = new DeferredSoundType(0.3F, 1.0F,
        DIG_GLASSWOOD, STEP_GLASSWOOD, STEP_GLASSWOOD, DIG_GLASSWOOD, STEP_GLASSWOOD);

    private ModSounds() {
    }

    private static DeferredHolder<SoundEvent, SoundEvent> sound(String path) {
        return SOUNDS.register(path, () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(ThaumicAdditions.MOD_ID, path)));
    }

    public static void register(IEventBus modEventBus) {
        SOUNDS.register(modEventBus);
    }
}
