package thaumicadditions.item;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import thaumicadditions.machine.Upgradable;
import thaumicadditions.machine.UpgradeType;

public final class UpgradeItem extends Item {
    private final UpgradeType type;

    public UpgradeItem(Properties properties, UpgradeType type) {
        super(properties.stacksTo(1));
        this.type = type;
    }

    public UpgradeType type() {
        return type;
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || !context.getLevel().mayInteract(player, context.getClickedPos())
            || !player.mayUseItemAt(context.getClickedPos(), context.getClickedFace(), stack)
            || !(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof Upgradable machine)
            || !machine.canInstall(type)) {
            return InteractionResult.PASS;
        }
        if (context.getLevel().isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!machine.installUpgrade(stack)) {
            return InteractionResult.PASS;
        }
        context.getLevel().playSound(null, context.getClickedPos(), thaumcraft.registry.ModSounds.UPGRADE.get(),
            net.minecraft.sounds.SoundSource.BLOCKS, 1, 1);
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.thaumicadditions.upgrade." + type.id()));
    }
}
