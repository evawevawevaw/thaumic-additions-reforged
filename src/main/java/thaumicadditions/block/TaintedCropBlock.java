package thaumicadditions.block;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.util.TriState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.taint.TaintBiomes;
import thaumicadditions.blockentity.TaintedCropBlockEntity;
import thaumicadditions.registry.ModBlockEntities;
import thaumicadditions.registry.ModItems;

public final class TaintedCropBlock extends Block implements EntityBlock, BonemealableBlock {
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 4);
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 11, 16);

    public TaintedCropBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState soil = level.getBlockState(pos.below());
        return soil.is(Blocks.FARMLAND) || soil.is(Blocks.DIRT) || soil.is(Blocks.GRASS_BLOCK)
            || soil.canSustainPlant(level, pos.below(), Direction.UP, state) == TriState.TRUE;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction side, BlockState neighbour, LevelAccessor level,
        BlockPos pos, BlockPos neighbourPos) {
        if (!canSurvive(state, level, pos)) {
            level.scheduleTick(pos, this, 1);
        }
        return super.updateShape(state, side, neighbour, level, pos, neighbourPos);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        if (state.getValue(AGE) < 3) {
            return List.of(new ItemStack(ModItems.TAINTED_SEED.get()));
        }
        Vec3 origin = params.getOptionalParameter(LootContextParams.ORIGIN);
        BlockPos pos = origin == null ? BlockPos.ZERO : BlockPos.containing(origin);
        int bound = TaintBiomes.isTainted(params.getLevel(), pos) ? 6 : 2;
        RandomSource random = params.getLevel().getRandom();
        return List.of(new ItemStack(ModItems.TAINTED_SEED.get(), 1 + random.nextInt(bound)),
            new ItemStack(ModItems.TAINTED_ROOTS.get(), 1 + random.nextInt(bound)));
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return new ItemStack(ModItems.TAINTED_SEED.get());
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return state.getValue(AGE) < 4;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        int age = state.getValue(AGE);
        level.setBlock(pos, state.setValue(AGE, Math.min(4, age + 1 + random.nextInt(Math.max(1, 3 - age)))), 3);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TaintedCropBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide || type != ModBlockEntities.TAINTED_CROPS.get() ? null :
            (tickLevel, pos, currentState, entity) -> {
                if (entity instanceof TaintedCropBlockEntity crop) {
                    crop.tickGrowth();
                }
            };
    }
}
