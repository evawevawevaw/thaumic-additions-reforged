package thaumicadditions.block;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.aspect.Aspect;
import thaumcraft.wand.WandInteractable;
import thaumcraft.wand.WandItem;
import thaumicadditions.blockentity.SealBlockEntity;
import thaumicadditions.item.SealItem;
import thaumicadditions.item.SealSymbolItem;
import thaumicadditions.registry.ModBlockEntities;
import thaumicadditions.registry.ModItems;
import thaumicadditions.registry.ModSounds;
import thaumicadditions.seal.SealBehaviors;

public final class SealBlock extends Block implements EntityBlock, WandInteractable {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    public SealBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case UP -> Block.box(0, 0, 0, 16, 1, 16);
            case DOWN -> Block.box(0, 15, 0, 16, 16, 16);
            case NORTH -> Block.box(0, 0, 15, 16, 16, 16);
            case SOUTH -> Block.box(0, 0, 0, 16, 16, 1);
            case WEST -> Block.box(15, 0, 0, 16, 16, 16);
            case EAST -> Block.box(0, 0, 0, 1, 16, 16);
        };
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SealBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide || type != ModBlockEntities.SEAL.get() ? null : (world, pos, current, entity) -> {
            if (entity instanceof SealBlockEntity seal) {
                seal.tick();
            }
        };
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (placer instanceof ServerPlayer player && level.getBlockEntity(pos) instanceof SealBlockEntity seal) {
            seal.install(SealItem.data(stack), player);
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult hit, LevelReader level, BlockPos pos, Player player) {
        return level.getBlockEntity(pos) instanceof SealBlockEntity seal ? seal.stack() : new ItemStack(ModItems.SEAL_ITEM.get());
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof SealBlockEntity seal
            ? List.of(seal.stack()) : List.of(new ItemStack(ModItems.SEAL_ITEM.get()));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
        Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof SealBlockEntity seal)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (stack.is(ModItems.SEAL_SYMBOL.get())) {
            Aspect aspect = SealSymbolItem.aspect(stack);
            if (aspect != null && seal.data().aspects().size() < 3) {
                if (!level.isClientSide) {
                    seal.setData(seal.data().append(aspect.getTag()));
                    stack.consume(1, player);
                    level.playSound(null, pos, ModSounds.SEAL_SYMBOL_SET.get(), SoundSource.BLOCKS, 1, 1);
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        if (stack.getItem() instanceof WandItem) {
            return removeSymbol(level, pos, player, seal) ? ItemInteractionResult.sidedSuccess(level.isClientSide)
                : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (stack.is(ModItems.SEAL_GLOBE.get())) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return activate(level, player, seal) ? ItemInteractionResult.sidedSuccess(level.isClientSide)
            : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private boolean removeSymbol(Level level, BlockPos pos, Player player, SealBlockEntity seal) {
        var symbols = seal.data().aspects();
        if (symbols.isEmpty()) {
            return false;
        }
        if (!level.isClientSide) {
            if (!player.isCreative()) {
                Block.popResource(level, pos, SealSymbolItem.symbol(symbols.getLast()));
            }
            seal.setData(seal.data().removeLast());
            level.playSound(null, pos, thaumcraft.registry.ModSounds.ZAP.get(), SoundSource.BLOCKS, 1, 1);
        }
        return true;
    }

    private boolean activate(Level level, Player player, SealBlockEntity seal) {
        return player instanceof ServerPlayer server && SealBehaviors.find(seal.data()).activate(seal, server);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player.getMainHandItem().is(ModItems.SEAL_GLOBE.get()) || player.getOffhandItem().is(ModItems.SEAL_GLOBE.get())) {
            return InteractionResult.PASS;
        }
        return level.getBlockEntity(pos) instanceof SealBlockEntity seal && activate(level, player, seal)
            ? InteractionResult.sidedSuccess(level.isClientSide) : InteractionResult.PASS;
    }

    @Override
    public InteractionResult onWandUse(Level level, BlockPos pos, Player player, ItemStack wand) {
        return level.getBlockEntity(pos) instanceof SealBlockEntity seal && removeSymbol(level, pos, player, seal)
            ? InteractionResult.sidedSuccess(level.isClientSide) : InteractionResult.PASS;
    }
}
