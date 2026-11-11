package thaumicadditions.registry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.FlowingFluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import thaumicadditions.ThaumicAdditions;
import thaumicadditions.block.PureWarpBlock;

public final class ModFluids {
    private static final DeferredRegister<FluidType> TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, ThaumicAdditions.MOD_ID);
    private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, ThaumicAdditions.MOD_ID);
    public static final DeferredHolder<FluidType, FluidType> PURE_WARP_TYPE = TYPES.register("pure_warp",
        () -> new FluidType(FluidType.Properties.create().viscosity(8000).canConvertToSource(false)));
    public static final DeferredHolder<Fluid, Source> PURE_WARP = FLUIDS.register("pure_warp", () -> new Source(properties()));
    public static final DeferredHolder<Fluid, Flowing> FLOWING_PURE_WARP = FLUIDS.register("flowing_pure_warp", () -> new Flowing(properties()));

    private ModFluids() {
    }

    private static BaseFlowingFluid.Properties properties() {
        return new BaseFlowingFluid.Properties(PURE_WARP_TYPE, PURE_WARP, FLOWING_PURE_WARP)
            .block(ModBlocks.PURE_WARP).bucket(ModItems.PURE_WARP_BUCKET).tickRate(PureWarpBlock.TICK_RATE);
    }

    private abstract static class Finite extends BaseFlowingFluid {
        private Finite(Properties properties) {
            super(properties);
        }

        @Override
        public void tick(Level level, BlockPos pos, FluidState state) {
            if (level instanceof ServerLevel server && level.getBlockState(pos).getBlock() instanceof PureWarpBlock block) {
                block.flow(server, pos, level.random);
            }
        }

        @Override
        public float getOwnHeight(FluidState state) {
            return getAmount(state) / 8.0F;
        }

        @Override
        protected BlockState createLegacyBlock(FluidState state) {
            return ModBlocks.PURE_WARP.get().defaultBlockState().setValue(LiquidBlock.LEVEL, 8 - getAmount(state));
        }

        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(FlowingFluid.LEVEL);
        }
    }

    public static final class Source extends Finite {
        private Source(Properties properties) {
            super(properties);
        }

        @Override
        public int getAmount(FluidState state) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }
    }

    public static final class Flowing extends Finite {
        private Flowing(Properties properties) {
            super(properties);
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(FlowingFluid.LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }
    }

    public static void register(IEventBus bus) {
        TYPES.register(bus);
        FLUIDS.register(bus);
    }
}
