package thaumicadditions.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import thaumcraft.item.gear.WarpingGear;
import thaumicadditions.crafting.DiceDrops;

public final class ArcaneDiceItem extends Item implements WarpingGear {
    public ArcaneDiceItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static int animationFrame(int remaining) {
        return remaining <= 0 ? 0 : Math.clamp(3 - (int)(remaining / 20.0F * 4 - 1), 0, 3);
    }

    @Override
    public int getWarp(ItemStack stack, Player player) {
        return 3;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 20;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remaining) {
        if (remaining != 1 || !(entity instanceof Player player)) {
            return;
        }
        InteractionHand hand = player.getUsedItemHand();
        player.stopUsingItem();
        if (!level.isClientSide && player.getItemInHand(hand) == stack) {
            ItemStack prize = DiceDrops.roll(level.random);
            if (!prize.isEmpty()) {
                player.setItemInHand(hand, prize);
                prize.onCraftedBy(level, player, 1);
            }
        }
    }
}
