package thaumicadditions.registry;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import thaumicadditions.ThaumicAdditions;
import thaumicadditions.entity.SingularityEntity;

@EventBusSubscriber(modid = ThaumicAdditions.MOD_ID)
public final class ModDispensers {
    private ModDispensers() {
    }

    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> DispenserBlock.registerBehavior(ModItems.SINGULARITY.get(), new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                Position position = DispenserBlock.getDispensePosition(source);
                Direction direction = source.state().getValue(DispenserBlock.FACING);
                SingularityEntity entity = new SingularityEntity(ModEntities.SINGULARITY.get(), source.level());
                entity.setPos(position.x(), position.y(), position.z());
                entity.shoot(direction.getStepX(), direction.getStepY() + 0.1, direction.getStepZ(), 1.1F, 6);
                if (source.level().addFreshEntity(entity) && !stack.getOrDefault(ModDataComponents.CREATIVE_DISPENSER.get(), false)) {
                    stack.shrink(1);
                }
                return stack;
            }

            @Override
            protected void playSound(BlockSource source) {
                source.level().levelEvent(1002, source.pos(), 0);
            }
        }));
    }
}
