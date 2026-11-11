package thaumicadditions.block;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import thaumcraft.registry.ModBlocks;
import thaumicadditions.registry.ModItems;
import thaumicadditions.registry.ModSounds;
import thaumicadditions.world.MonolithStructure;

public final class EldritchLockBlock extends Block {
    public static final BooleanProperty WALL = BooleanProperty.create("wall");
    private static final Direction[] DIRECTIONS = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    public EldritchLockBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(WALL, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WALL);
    }

    private static BlockState crystal(int index) {
        return switch (index) {
            case 0 -> ModBlocks.AIR_CRYSTAL_CLUSTER.get().defaultBlockState();
            case 1 -> ModBlocks.FIRE_CRYSTAL_CLUSTER.get().defaultBlockState();
            case 2 -> ModBlocks.WATER_CRYSTAL_CLUSTER.get().defaultBlockState();
            default -> ModBlocks.EARTH_CRYSTAL_CLUSTER.get().defaultBlockState();
        };
    }

    private boolean activate(BlockState state, Level level, BlockPos pos, Player player, ItemStack stack, BlockHitResult hit) {
        if (state.getValue(WALL)) {
            if (!stack.is(ModItems.ELDRITCH_KEYSTONE_TLHUTLH.get()) || !hit.getDirection().getAxis().isHorizontal()) {
                return false;
            }
            if (player instanceof ServerPlayer server && MonolithStructure.apply(level,
                MonolithStructure.room(pos, hit.getDirection().getOpposite()), server, true)) {
                stack.consume(1, player);
                level.playSound(null, pos, ModSounds.PORTALOPEN.get(), SoundSource.BLOCKS, 1, 1);
            }
            return true;
        }
        boolean complete = true;
        for (int index = 0; index < DIRECTIONS.length; index++) {
            complete &= level.getBlockState(pos.above().relative(DIRECTIONS[index])).is(crystal(index).getBlock());
        }
        if (complete) {
            if (player instanceof ServerPlayer server && MonolithStructure.apply(level,
                MonolithStructure.entrance(pos), server, false)) {
                level.playSound(null, pos, ModSounds.PORTALOPEN.get(), SoundSource.BLOCKS, 1, 1);
            }
            return true;
        }
        for (int index = 0; index < DIRECTIONS.length; index++) {
            if (stack.is(crystal(index).getBlock().asItem())) {
                BlockPos target = pos.above().relative(DIRECTIONS[index]);
                if (!level.isEmptyBlock(target)) {
                    return false;
                }
                if (player instanceof ServerPlayer server && MonolithStructure.apply(level, Map.of(target, crystal(index)), server, false)) {
                    stack.consume(1, player);
                    level.playSound(null, pos, ModSounds.SEAL_SYMBOL_SET.get(), SoundSource.BLOCKS, 1, 1);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
        Player player, InteractionHand hand, BlockHitResult hit) {
        return activate(state, level, pos, player, stack, hit) ? ItemInteractionResult.sidedSuccess(level.isClientSide)
            : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        return activate(state, level, pos, player, ItemStack.EMPTY, hit) ? InteractionResult.sidedSuccess(level.isClientSide)
            : InteractionResult.PASS;
    }
}
