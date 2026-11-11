package thaumicadditions.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import thaumcraft.wand.focus.FocusItem;

public abstract class AddonFocusItem extends FocusItem {
    private final String texture;

    protected AddonFocusItem(Properties properties, String texture) {
        super(FocusItem.properties(properties));
        this.texture = texture;
    }

    @Override
    public ResourceLocation getFocusTexture(ItemStack stack) {
        return ResourceLocation.parse("thaumicadditions:item/foci/" + texture);
    }

    @Override
    public int getFocusColor(ItemStack stack) {
        return 0xFFFFFF;
    }

    @Override
    public String getSortingHelper(ItemStack stack) {
        return "PRJ" + super.getSortingHelper(stack);
    }

    @Override
    public ResourceLocation getOrnamentTexture(ItemStack stack) {
        return ResourceLocation.parse("thaumicadditions:item/foci/" + texture + "_orn");
    }

    @Override
    public ResourceLocation getDepthLayerTexture(ItemStack stack) {
        return ResourceLocation.parse("thaumicadditions:item/foci/" + texture + "_depth");
    }
}
