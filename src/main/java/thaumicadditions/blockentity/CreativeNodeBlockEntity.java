package thaumicadditions.blockentity;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.node.Node;
import thaumcraft.node.NodeModifier;
import thaumcraft.node.NodeType;
import thaumcraft.vis.VisNode;
import thaumcraft.wand.WandInteractable;
import thaumcraft.wand.WandKeys;
import thaumcraft.wand.Wands;
import thaumicadditions.registry.ModBlockEntities;

public final class CreativeNodeBlockEntity extends VisNode implements Node, WandInteractable {
    public CreativeNodeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CREATIVE_NODE.get(), pos, state);
    }

    public void tick() {
        if (level != null) {
            tickNetwork(level);
        }
    }

    @Override
    public String getId() {
        String dimension = level == null ? "unknown" : level.dimension().location().toString();
        return dimension + ":" + worldPosition.getX() + ":" + worldPosition.getY() + ":" + worldPosition.getZ();
    }

    @Override
    public AspectList getAspects() {
        AspectList result = new AspectList();
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            result.add(aspect, 134217);
        }
        return result;
    }

    @Override
    public AspectList getAspectsBase() {
        return getAspects();
    }

    @Override
    public void setAspects(AspectList aspects) {
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.PURE;
    }

    @Override
    public void setNodeType(NodeType type) {
    }

    @Override
    public NodeModifier getNodeModifier() {
        return NodeModifier.BRIGHT;
    }

    @Override
    public void setNodeModifier(NodeModifier modifier) {
    }

    @Override
    public int getNodeVisBase(Aspect aspect) {
        return Integer.MAX_VALUE;
    }

    @Override
    public void setNodeVisBase(Aspect aspect, int amount) {
    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        return false;
    }

    @Override
    public int addToContainer(Aspect aspect, int amount) {
        return Math.max(0, amount);
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int amount) {
        return aspect != null && amount >= 0;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect aspect, int amount) {
        return aspect != null && amount >= 0;
    }

    @Override
    public int containerContains(Aspect aspect) {
        return aspect == null ? 0 : Integer.MAX_VALUE;
    }

    @Override
    public int getRange() {
        return 16;
    }

    @Override
    public boolean isSource() {
        return true;
    }

    @Override
    public int consumeVis(Aspect aspect, int amount) {
        return aspect == null ? 0 : Math.max(0, amount);
    }

    @Override
    public InteractionResult onWandUse(Level useLevel, BlockPos pos, Player player, ItemStack wand) {
        if (!useLevel.mayInteract(player, pos) || player.isSpectator()) {
            return InteractionResult.FAIL;
        }
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            thaumicadditions.advancement.ModAdvancements.award(serverPlayer, "creative_node");
        }
        WandKeys.TARGET.set(wand, worldPosition);
        return InteractionResult.CONSUME;
    }

    @Override
    public void onUsingWandTick(ItemStack wand, Player player, int remaining) {
        HitResult hit = player.pick(player.blockInteractionRange(), 1, true);
        if (!(hit instanceof BlockHitResult blockHit) || hit.getType() != HitResult.Type.BLOCK
            || !blockHit.getBlockPos().equals(worldPosition) || player.level() != level || player.isSpectator()
            || !level.mayInteract(player, worldPosition)) {
            player.stopUsingItem();
            return;
        }
        if (level.isClientSide) {
            return;
        }
        AspectList room = Wands.getAspectsWithRoom(wand);
        List<Aspect> candidates = Aspect.getPrimalAspects().stream().filter(aspect -> room.getAmount(aspect) > 0).toList();
        if (!candidates.isEmpty()) {
            Aspect chosen = candidates.get(level.random.nextInt(candidates.size()));
            Wands.addVis(wand, chosen, 100, true);
        }
    }
}
