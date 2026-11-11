package thaumicadditions.registry;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumicadditions.ThaumicAdditions;

public final class ModDataComponents {
    private static final DeferredRegister.DataComponents COMPONENTS =
        DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ThaumicAdditions.MOD_ID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SWORD_AURA_MODE =
        COMPONENTS.registerComponentType("sword_aura_mode", builder -> builder
            .persistent(Codec.intRange(0, 2)).networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SWORD_AURA_TICKS =
        COMPONENTS.registerComponentType("sword_aura_ticks", builder -> builder
            .networkSynchronized(ByteBufCodecs.VAR_INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ARMOR_VIS_CHARGE =
        COMPONENTS.registerComponentType("armor_vis_charge", builder -> builder
            .persistent(Codec.intRange(0, 80000)).networkSynchronized(ByteBufCodecs.VAR_INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<thaumicadditions.item.TransportedBlock>> TRANSPORTED_BLOCK =
        COMPONENTS.registerComponentType("transported_block", builder -> builder
            .persistent(thaumicadditions.item.TransportedBlock.CODEC)
            .networkSynchronized(ByteBufCodecs.fromCodecWithRegistries(thaumicadditions.item.TransportedBlock.CODEC)));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<thaumicadditions.item.RelicOwner>> RELIC_OWNER =
        COMPONENTS.registerComponentType("relic_owner", builder -> builder
            .persistent(thaumicadditions.item.RelicOwner.CODEC)
            .networkSynchronized(ByteBufCodecs.fromCodecWithRegistries(thaumicadditions.item.RelicOwner.CODEC)));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> CREATIVE_DISPENSER =
        COMPONENTS.registerComponentType("creative_dispenser", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> VOID_CHEST_LOOT =
        COMPONENTS.registerComponentType("void_chest_loot", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<net.minecraft.core.GlobalPos>> RETURN_DESTINATION =
        COMPONENTS.registerComponentType("return_destination", builder -> builder.persistent(net.minecraft.core.GlobalPos.CODEC)
            .networkSynchronized(ByteBufCodecs.fromCodecWithRegistries(net.minecraft.core.GlobalPos.CODEC)));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<thaumicadditions.seal.SealData>> SEAL_DATA =
        COMPONENTS.registerComponentType("seal_data", builder -> builder.persistent(thaumicadditions.seal.SealData.CODEC)
            .networkSynchronized(ByteBufCodecs.fromCodecWithRegistries(thaumicadditions.seal.SealData.CODEC)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> SYMBOL_ASPECT =
        COMPONENTS.registerComponentType("symbol_aspect", builder -> builder.persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8));

    private ModDataComponents() {
    }

    public static void register(IEventBus modEventBus) {
        COMPONENTS.register(modEventBus);
    }
}
