package thaumicadditions.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import thaumicadditions.entity.SingularityEntity;
import thaumicadditions.registry.ModDataComponents;

public final class SingularityItem extends Item {
    public SingularityItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && level.addFreshEntity(SingularityEntity.thrown(level, player)) && !player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (stack.getOrDefault(ModDataComponents.CREATIVE_DISPENSER.get(), false)) {
            tooltip.add(Component.translatable("tooltip.thaumicadditions.infinite_dispenser").withStyle(ChatFormatting.YELLOW));
        }
    }
}
