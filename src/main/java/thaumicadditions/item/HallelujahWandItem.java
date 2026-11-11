package thaumicadditions.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thaumicadditions.damage.ModDamageTypes;
import thaumicadditions.registry.ModDataComponents;
import thaumicadditions.registry.ModSounds;

public final class HallelujahWandItem extends Item {
    public HallelujahWandItem(Properties properties) {
        super(properties);
    }

    public static void bind(ItemStack stack, Player player) {
        if (!player.level().isClientSide && !stack.has(ModDataComponents.RELIC_OWNER.get())) {
            stack.set(ModDataComponents.RELIC_OWNER.get(), new RelicOwner(player.getUUID(), player.getDisplayName().getString()));
        }
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        bind(stack, player);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide) {
            return;
        }
        if (entity instanceof Player player) {
            bind(stack, player);
            RelicOwner owner = stack.get(ModDataComponents.RELIC_OWNER.get());
            if (owner != null && !owner.id().equals(player.getUUID())) {
                player.hurt(level.damageSources().source(ModDamageTypes.INVALID_RELIC_OWNER), 2);
            }
        } else {
            entity.hurt(level.damageSources().source(ModDamageTypes.RELIC_NONPLAYER), 100500);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 100;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        bind(player.getItemInHand(hand), player);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remaining) {
        if (remaining != 1 || !(entity instanceof Player player)) {
            return;
        }
        player.stopUsingItem();
        if (!level.isClientSide) {
            PrimedTnt tnt = new PrimedTnt(level, player.getX(), player.getY() + 1, player.getZ(), player);
            tnt.setFuse(460);
            tnt.setDeltaMovement(Vec3.ZERO);
            level.addFreshEntity(tnt);
            level.playSound(null, player.blockPosition(), ModSounds.HALLELUJAH.get(), SoundSource.PLAYERS, 4.95F, 1);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        RelicOwner owner = stack.get(ModDataComponents.RELIC_OWNER.get());
        if (owner != null) {
            tooltip.add(Component.translatable("tooltip.thaumicadditions.relic_owner", owner.name()).withStyle(ChatFormatting.GRAY));
        }
    }
}
