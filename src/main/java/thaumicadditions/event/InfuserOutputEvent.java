package thaumicadditions.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import thaumicadditions.blockentity.InfuserBlockEntity;

public final class InfuserOutputEvent extends Event implements ICancellableEvent {
    private final InfuserBlockEntity machine;
    private final Player player;
    private final ItemStack output;

    public InfuserOutputEvent(InfuserBlockEntity machine, Player player, ItemStack output) {
        this.machine = machine;
        this.player = player;
        this.output = output.copy();
    }

    public InfuserBlockEntity machine() {
        return machine;
    }

    public Player player() {
        return player;
    }

    public ItemStack output() {
        return output.copy();
    }
}
