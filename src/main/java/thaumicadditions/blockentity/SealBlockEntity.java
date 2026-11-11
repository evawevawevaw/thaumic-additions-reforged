package thaumicadditions.blockentity;

import com.mojang.authlib.GameProfile;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import thaumcraft.aspect.Aspect;
import thaumcraft.vis.VisNet;
import thaumicadditions.registry.ModBlockEntities;
import thaumicadditions.registry.ModDataComponents;
import thaumicadditions.registry.ModItems;
import thaumicadditions.seal.SealBehaviors;
import thaumicadditions.seal.SealData;
import thaumicadditions.world.MonolithStructure;

public final class SealBlockEntity extends BlockEntity {
    private SealData data = SealData.EMPTY;
    private UUID owner;
    private String ownerName = "ThaumicSeal";
    private final Map<String, Integer> reserve = new HashMap<>();

    public SealBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SEAL.get(), pos, state);
    }

    public SealData data() {
        return data;
    }

    public void install(SealData data, ServerPlayer player) {
        this.data = data;
        owner = player.getUUID();
        ownerName = player.getGameProfile().getName();
        sync();
    }

    public void setData(SealData data) {
        this.data = data;
        sync();
    }

    public void tick() {
        if (level instanceof ServerLevel) {
            SealBehaviors.find(data).tick(this);
        }
    }

    public ServerPlayer owner() {
        if (!(level instanceof ServerLevel server) || owner == null) {
            return null;
        }
        ServerPlayer player = server.getServer().getPlayerList().getPlayer(owner);
        return player == null || player.serverLevel() != server
            ? FakePlayerFactory.get(server, new GameProfile(owner, ownerName)) : player;
    }

    public boolean replace(BlockPos pos, BlockState state) {
        ServerPlayer player = owner();
        return player != null && level instanceof ServerLevel server
            && MonolithStructure.apply(server, Map.of(pos.immutable(), state), player, false);
    }

    public boolean payVis(BlockPos source, Aspect aspect, int cost) {
        int stored = reserve.getOrDefault(aspect.getTag(), 0);
        int updated = stored + VisNet.drainVis(level, source, aspect, Math.max(0, cost - stored));
        boolean paid = updated >= cost;
        int remaining = paid ? updated - cost : updated;
        if (stored != remaining) {
            reserve.put(aspect.getTag(), remaining);
            setChanged();
        }
        return paid;
    }

    public ItemStack stack() {
        ItemStack stack = new ItemStack(ModItems.SEAL_ITEM.get());
        stack.set(ModDataComponents.SEAL_DATA.get(), data);
        return stack;
    }

    private void sync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("seal", SealData.CODEC.encodeStart(NbtOps.INSTANCE, data).getOrThrow());
        if (owner != null) {
            tag.putUUID("owner", owner);
            tag.putString("owner_name", ownerName);
        }
        CompoundTag charges = new CompoundTag();
        reserve.forEach(charges::putInt);
        tag.put("vis_reserve", charges);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        data = SealData.CODEC.parse(NbtOps.INSTANCE, tag.getCompound("seal")).result().orElse(SealData.EMPTY);
        owner = tag.hasUUID("owner") ? tag.getUUID("owner") : null;
        ownerName = tag.contains("owner_name") ? tag.getString("owner_name") : "ThaumicSeal";
        reserve.clear();
        CompoundTag charges = tag.getCompound("vis_reserve");
        for (String key : charges.getAllKeys()) {
            reserve.put(key, Math.clamp(charges.getInt(key), 0, 1000));
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.put("seal", SealData.CODEC.encodeStart(NbtOps.INSTANCE, data).getOrThrow());
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
