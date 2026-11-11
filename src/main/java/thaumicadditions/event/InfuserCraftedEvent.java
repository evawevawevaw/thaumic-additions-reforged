package thaumicadditions.event;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import thaumicadditions.blockentity.InfuserBlockEntity;
import thaumicadditions.crafting.InfuserRecipe;

public final class InfuserCraftedEvent extends Event {
    private final InfuserBlockEntity machine;
    private final InfuserRecipe recipe;
    private final List<ItemStack> inputs;
    private final ItemStack result;

    public InfuserCraftedEvent(InfuserBlockEntity machine, InfuserRecipe recipe, List<ItemStack> inputs, ItemStack result) {
        this.machine = machine;
        this.recipe = recipe;
        this.inputs = inputs.stream().map(ItemStack::copy).toList();
        this.result = result.copy();
    }

    public InfuserBlockEntity machine() {
        return machine;
    }

    public InfuserRecipe recipe() {
        return recipe;
    }

    public List<ItemStack> inputs() {
        return inputs.stream().map(ItemStack::copy).toList();
    }

    public ItemStack result() {
        return result.copy();
    }
}
