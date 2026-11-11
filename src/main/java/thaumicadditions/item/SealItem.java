package thaumicadditions.item;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import thaumcraft.aspect.Aspect;
import thaumicadditions.registry.ModDataComponents;
import thaumicadditions.seal.SealData;

public final class SealItem extends BlockItem {
    public SealItem(Block block, Properties properties) {
        super(block, properties.stacksTo(1).component(ModDataComponents.SEAL_DATA.get(), SealData.EMPTY));
    }

    public static SealData data(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.SEAL_DATA.get(), SealData.EMPTY);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        for (String tag : data(stack).aspects()) {
            Aspect aspect = Aspect.getAspect(tag);
            if (aspect != null) {
                tooltip.add(aspect.getDescription());
            }
        }
    }
}
