package thaumicadditions.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thaumicadditions.menu.InfusionBookMenu;

public final class InfusionBookItem extends Item {
    private final boolean dark;

    public InfusionBookItem(Properties properties, boolean dark) {
        super(properties.stacksTo(1));
        this.dark = dark;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack book = player.getItemInHand(hand);
        if (player instanceof ServerPlayer server) {
            server.openMenu(new SimpleMenuProvider((id, inventory, owner) -> new InfusionBookMenu(id, inventory, dark),
                book.getHoverName()), buffer -> buffer.writeBoolean(dark));
        }
        return InteractionResultHolder.sidedSuccess(book, level.isClientSide);
    }
}
