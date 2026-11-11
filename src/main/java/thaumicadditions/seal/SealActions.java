package thaumicadditions.seal;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import thaumcraft.alchemy.FluxBlock;
import thaumcraft.aspect.Aspect;
import thaumcraft.registry.ModSounds;
import thaumcraft.research.ResearchManager;
import thaumcraft.research.Warp;
import thaumcraft.research.WarpType;
import thaumicadditions.blockentity.ReturnerBlockEntity;
import thaumicadditions.blockentity.SealBlockEntity;
import thaumicadditions.registry.ModBlocks;

public final class SealActions {
    private SealActions() {
    }

    private static ServerLevel level(SealBlockEntity seal) {
        return (ServerLevel)seal.getLevel();
    }

    private static AABB box(SealBlockEntity seal, int radius) {
        BlockPos pos = seal.getBlockPos();
        return new AABB(pos.getX() - radius, pos.getY() - radius, pos.getZ() - radius,
            pos.getX() + radius, pos.getY() + radius, pos.getZ() + radius);
    }

    private static List<ItemEntity> nearItems(SealBlockEntity seal) {
        BlockPos pos = seal.getBlockPos();
        return level(seal).getEntitiesOfClass(ItemEntity.class,
            new AABB(pos.getX() - 1, pos.getY(), pos.getZ() - 1, pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1),
            entity -> entity.isAlive() && !entity.getItem().isEmpty());
    }

    public static void repair(SealBlockEntity seal) {
        for (ItemEntity item : nearItems(seal)) {
            ItemStack stack = item.getItem();
            if (stack.isDamageableItem() && stack.isDamaged() && seal.payVis(item.blockPosition(), Aspect.EARTH, 1)) {
                ItemStack updated = stack.copy();
                updated.setDamageValue(updated.getDamageValue() - 1);
                item.setItem(updated);
                level(seal).sendParticles(ParticleTypes.FLAME, item.getX(), item.getY(), item.getZ(), 1, 0, 0.05, 0, 0);
                return;
            }
        }
    }

    public static void clearFlux(SealBlockEntity seal) {
        ServerLevel level = level(seal);
        BlockPos center = seal.getBlockPos();
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-16, -16, -16), center.offset(15, 15, 15))) {
            if (!level.hasChunk(pos.getX() >> 4, pos.getZ() >> 4)) {
                continue;
            }
            BlockState state = level.getBlockState(pos);
            if (state.getBlock() instanceof FluxBlock) {
                int amount = state.getValue(FluxBlock.LEVEL);
                BlockState result = amount == 1 ? Blocks.AIR.defaultBlockState() : state.setValue(FluxBlock.LEVEL, amount - 1);
                if (seal.replace(pos, result)) {
                    return;
                }
            }
        }
    }

    public static void smelt(SealBlockEntity seal) {
        ServerLevel level = level(seal);
        for (ItemEntity item : nearItems(seal)) {
            if (item.getAge() < 60) {
                continue;
            }
            ItemStack input = item.getItem();
            var recipeInput = new SingleRecipeInput(input);
            var recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, recipeInput, level);
            if (recipe.isEmpty()) {
                continue;
            }
            ItemStack result = recipe.get().value().assemble(recipeInput, level.registryAccess());
            if (result.isEmpty()) {
                continue;
            }
            int count = 0;
            while (!input.isEmpty() && seal.payVis(item.blockPosition(), Aspect.FIRE, 2)) {
                ItemEntity output = new ItemEntity(level, item.getX(), item.getY(), item.getZ(), result.copy());
                output.setDeltaMovement(item.getDeltaMovement());
                output.setNoPickUpDelay();
                if (!level.addFreshEntity(output)) {
                    break;
                }
                input.shrink(1);
                count++;
            }
            item.setItem(input.copy());
            if (input.isEmpty()) {
                item.discard();
            }
            if (count > 0) {
                level.sendParticles(ParticleTypes.FLAME, item.getX(), item.getY(), item.getZ(), 20, 0.2, 0.1, 0.2, 0.05);
                level.playSound(null, item.blockPosition(), ModSounds.FIRELOOP.get(), SoundSource.BLOCKS, 0.3F, 1);
            }
        }
    }

    public static void whisper(SealBlockEntity seal) {
        ServerLevel level = level(seal);
        for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, box(seal, 5), Player::isAlive)) {
            if (level.random.nextInt(800) < 5) {
                var primals = Aspect.getPrimalAspects();
                Aspect aspect = primals.get(level.random.nextInt(primals.size()));
                if (seal.payVis(player.blockPosition(), aspect, 10)) {
                    ResearchManager.addAspectPool(player, aspect, 1);
                }
            }
        }
    }

    public static void warp(SealBlockEntity seal) {
        ServerLevel level = level(seal);
        for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, box(seal, 5), Player::isAlive)) {
            if (level.random.nextInt(800) < 5) {
                Warp.add(player, level.random.nextInt(5), WarpType.STICKY);
                level.playSound(null, player.blockPosition(), ModSounds.WHISPERS.get(), SoundSource.PLAYERS, 2, 1);
            }
        }
    }

    public static void collect(SealBlockEntity seal) {
        ServerLevel level = level(seal);
        BlockPos pos = seal.getBlockPos();
        for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, box(seal, 16), entity -> entity.isAlive() && !entity.getItem().isEmpty())) {
            if (item.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) >= 1) {
                item.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                item.setDeltaMovement(0, -1, 0);
                level.playSound(null, pos, ModSounds.ZAP.get(), SoundSource.BLOCKS, 1, 1);
                return;
            }
        }
    }

    public static void harvest(SealBlockEntity seal) {
        ServerLevel level = level(seal);
        BlockPos center = seal.getBlockPos();
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-8, -8, -8), center.offset(8, 8, 8))) {
            if (!level.hasChunk(pos.getX() >> 4, pos.getZ() >> 4)) {
                continue;
            }
            BlockState state = level.getBlockState(pos);
            ItemStack crop = ItemStack.EMPTY;
            ItemStack extra = ItemStack.EMPTY;
            BlockState reset = state;
            if (state.is(Blocks.WHEAT) || state.is(Blocks.CARROTS) || state.is(Blocks.POTATOES)) {
                CropBlock block = (CropBlock)state.getBlock();
                if (!block.isMaxAge(state)) {
                    continue;
                }
                reset = block.getStateForAge(0);
                crop = new ItemStack(state.is(Blocks.WHEAT) ? Items.WHEAT : state.is(Blocks.CARROTS) ? Items.CARROT : Items.POTATO,
                    1 + level.random.nextInt(2));
                if (state.is(Blocks.WHEAT)) {
                    extra = new ItemStack(Items.WHEAT_SEEDS, 1 + level.random.nextInt(2));
                } else if (state.is(Blocks.POTATOES) && level.random.nextInt(100) < 5) {
                    extra = new ItemStack(Items.POISONOUS_POTATO);
                }
            } else if (state.is(Blocks.NETHER_WART) && state.getValue(NetherWartBlock.AGE) == 3) {
                reset = state.setValue(NetherWartBlock.AGE, 0);
                crop = new ItemStack(Items.NETHER_WART, 1 + level.random.nextInt(2));
            }
            if (!crop.isEmpty() && seal.replace(pos, reset)) {
                Block.popResource(level, pos, crop);
                Block.popResource(level, pos, extra);
                return;
            }
        }
    }

    public static void soil(SealBlockEntity seal) {
        ServerLevel level = level(seal);
        BlockPos center = seal.getBlockPos().below();
        var symbols = seal.data().aspects();
        boolean wet = symbols.size() == 3 && symbols.get(2).equals("aqua");
        boolean undo = symbols.size() == 3 && symbols.get(2).equals("perditio");
        int radius = symbols.size() == 2 ? 3 : 6;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                BlockPos pos = center.offset(x, 0, z);
                if (x == 0 && z == 0 || !level.hasChunk(pos.getX() >> 4, pos.getZ() >> 4)) {
                    continue;
                }
                BlockState state = level.getBlockState(pos);
                if (undo) {
                    if (state.is(Blocks.FARMLAND) && seal.replace(pos, Blocks.GRASS_BLOCK.defaultBlockState())) {
                        return;
                    }
                } else if ((state.is(Blocks.DIRT) || state.is(Blocks.GRASS_BLOCK)) && level.isEmptyBlock(pos.above())) {
                    if (seal.replace(pos, Blocks.FARMLAND.defaultBlockState())) {
                        return;
                    }
                } else if (wet && state.is(Blocks.FARMLAND)) {
                    if (state.getValue(FarmBlock.MOISTURE) != 1 && seal.replace(pos, state.setValue(FarmBlock.MOISTURE, 1))) {
                        return;
                    }
                    if (plant(seal, pos)) {
                        return;
                    }
                }
            }
        }
    }

    private static boolean plant(SealBlockEntity seal, BlockPos soil) {
        ServerLevel level = level(seal);
        BlockPos inventoryPos = seal.getBlockPos().above();
        if (!level.isEmptyBlock(soil.above()) || seal.owner() == null || !level.mayInteract(seal.owner(), inventoryPos)) {
            return false;
        }
        var handler = level.getCapability(Capabilities.ItemHandler.BLOCK, inventoryPos, Direction.DOWN);
        if (handler == null) {
            return false;
        }
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack available = handler.extractItem(slot, 1, true);
            Block crop = available.is(Items.WHEAT_SEEDS) ? Blocks.WHEAT : available.is(Items.CARROT) ? Blocks.CARROTS
                : available.is(Items.POTATO) ? Blocks.POTATOES : null;
            if (crop == null) {
                continue;
            }
            ItemStack taken = handler.extractItem(slot, 1, false);
            if (taken.isEmpty()) {
                continue;
            }
            if (seal.replace(soil.above(), crop.defaultBlockState())) {
                return true;
            }
            ItemStack leftover = ItemHandlerHelper.insertItemStacked(handler, taken, false);
            Block.popResource(level, inventoryPos, leftover);
        }
        return false;
    }

    public static void protect(SealBlockEntity seal) {
        ServerLevel level = level(seal);
        BlockPos pos = seal.getBlockPos();
        AABB bounds = new AABB(pos.getX() - 15.5, pos.getY() - 15.5, pos.getZ() - 15.5,
            pos.getX() + 16.5, pos.getY() + 26.5, pos.getZ() + 16.5);
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, bounds, LivingEntity::isAlive)) {
            level.sendParticles(ParticleTypes.ENCHANT, entity.getX(), entity.getY(), entity.getZ(), 1, 0.2, 0.2, 0.2, 0);
            if (!(entity instanceof Player)) {
                entity.hurt(seal.owner() == null ? level.damageSources().magic() : level.damageSources().playerAttack(seal.owner()), 1);
                Vec3 pull = pos.getCenter().subtract(entity.position()).scale(0.05);
                entity.setDeltaMovement(entity.getDeltaMovement().add(pull).scale(-1));
            }
        }
    }

    public static boolean nether(SealBlockEntity seal, ServerPlayer player) {
        if (!player.isShiftKeyDown()) {
            return false;
        }
        ServerLevel target = player.server.getLevel(Level.NETHER);
        if (target == null) {
            return false;
        }
        GlobalPos returnTo = GlobalPos.of(player.serverLevel().dimension(), seal.getBlockPos());
        Vec3 previous = player.position();
        double scale = net.minecraft.world.level.dimension.DimensionType.getTeleportationScale(player.level().dimensionType(), target.dimensionType());
        BlockPos column = BlockPos.containing(player.getX() * scale, player.getY(), player.getZ() * scale);
        BlockPos arrival = null;
        for (int y = Math.min(target.getMaxBuildHeight() - 3, 120); y >= target.getMinBuildHeight() + 1; y--) {
            BlockPos candidate = new BlockPos(column.getX(), y, column.getZ());
            if (target.getWorldBorder().isWithinBounds(candidate) && target.isEmptyBlock(candidate)
                && target.isEmptyBlock(candidate.above()) && target.getBlockState(candidate.below()).isFaceSturdy(target, candidate.below(), Direction.UP)) {
                arrival = candidate;
                break;
            }
        }
        if (arrival == null || !target.mayInteract(player, arrival)) {
            return false;
        }
        player.teleportTo(target, arrival.getX() + 0.5, arrival.getY(), arrival.getZ() + 0.5, player.getYRot(), player.getXRot());
        if (player.serverLevel() != target) {
            return false;
        }
        if (!thaumicadditions.world.MonolithStructure.apply(target,
            java.util.Map.of(arrival, ModBlocks.RETURNER.get().defaultBlockState()), player, false)) {
            player.teleportTo(level(seal), previous.x, previous.y, previous.z,
                player.getYRot(), player.getXRot());
            return false;
        }
        if (target.getBlockEntity(arrival) instanceof ReturnerBlockEntity returner) {
            returner.setDestination(returnTo);
        }
        player.setDeltaMovement(Vec3.ZERO);
        player.resetFallDistance();
        return true;
    }
}
