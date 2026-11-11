package thaumicadditions.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.research.Warp;
import thaumcraft.research.WarpType;
import thaumicadditions.registry.ModFluids;
import thaumicadditions.machine.FiniteFluidFlow;

public final class PureWarpBlock extends LiquidBlock {
    public static final int TICK_RATE = 40;

    public PureWarpBlock(Properties properties) {
        super(ModFluids.PURE_WARP.get(), properties);
    }

    private int amount(BlockState state) {
        return state.is(this) ? Math.clamp(8 - state.getValue(LEVEL), 0, 8) : 0;
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        int amount = amount(state);
        return amount == 8 ? ModFluids.PURE_WARP.get().getSource(false) : amount > 0 ?
            ModFluids.FLOWING_PURE_WARP.get().getFlowing(amount, false) : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected boolean canBeReplaced(BlockState state, Fluid fluid) {
        return false;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState old, boolean moved) {
        level.scheduleTick(pos, ModFluids.PURE_WARP.get(), TICK_RATE);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighbour, boolean moved) {
        level.scheduleTick(pos, ModFluids.PURE_WARP.get(), TICK_RATE);
    }

    private int capacity(ServerLevel level, BlockPos pos) {
        if (!level.isInWorldBounds(pos) || !level.isLoaded(pos)) {
            return 0;
        }
        BlockState state = level.getBlockState(pos);
        if (state.is(this)) {
            return 8 - amount(state);
        }
        if (state.isAir()) {
            return 8;
        }
        if (state.hasBlockEntity() || !state.getFluidState().isEmpty() || state.is(BlockTags.DOORS)
            || state.is(BlockTags.SIGNS) || state.is(Blocks.SUGAR_CANE) || state.is(Blocks.NETHER_PORTAL)
            || state.is(Blocks.END_PORTAL) || state.getDestroySpeed(level, pos) < 0
            || !state.getCollisionShape(level, pos).isEmpty()) {
            return 0;
        }
        return 8;
    }

    private void setAmount(ServerLevel level, BlockPos pos, int amount) {
        BlockState previous = level.getBlockState(pos);
        if (amount > 0 && !previous.isAir() && !previous.is(this)) {
            Block.dropResources(previous, level, pos);
        }
        BlockState state = amount == 0 ? Blocks.AIR.defaultBlockState() : defaultBlockState().setValue(LEVEL, 8 - amount);
        if (previous != state) {
            level.setBlock(pos, state, Block.UPDATE_ALL);
        }
        if (amount > 0) {
            level.scheduleTick(pos, ModFluids.PURE_WARP.get(), TICK_RATE);
        }
    }

    public void flow(ServerLevel level, BlockPos pos, RandomSource random) {
        int remaining = amount(level.getBlockState(pos));
        if (remaining <= 0) {
            return;
        }
        BlockPos below = pos.below();
        int down = FiniteFluidFlow.downward(remaining, 8 - capacity(level, below));
        if (down > 0) {
            int existing = amount(level.getBlockState(below));
            remaining -= down;
            setAmount(level, pos, remaining);
            setAmount(level, below, existing + down);
        }
        if (remaining <= 1) {
            return;
        }
        Direction[] sides = {Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
        int[] neighbours = new int[4];
        for (int index = 0; index < sides.length; index++) {
            BlockPos neighbour = pos.relative(sides[index]);
            neighbours[index] = capacity(level, neighbour) > 0 ? amount(level.getBlockState(neighbour)) : -1;
        }
        int[] result = FiniteFluidFlow.balance(remaining, neighbours, random.nextInt(4));
        if (result[0] == remaining) {
            return;
        }
        setAmount(level, pos, result[0]);
        for (int index = 0; index < sides.length; index++) {
            if (result[index + 1] >= 0 && result[index + 1] != neighbours[index]) {
                setAmount(level, pos.relative(sides[index]), result[index + 1]);
            }
        }
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof ServerPlayer player && level.random.nextInt(100) == 0) {
            Warp.add(player, 1, WarpType.STICKY);
        }
    }
}
