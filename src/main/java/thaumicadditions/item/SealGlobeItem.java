package thaumicadditions.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import thaumicadditions.blockentity.SealBlockEntity;
import thaumicadditions.menu.SealGlobeMenu;

public final class SealGlobeItem extends Item {
    public SealGlobeItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var pos = context.getClickedPos();
        if (!(context.getLevel().getBlockEntity(pos) instanceof SealBlockEntity)) {
            return InteractionResult.PASS;
        }
        if (context.getPlayer() instanceof ServerPlayer player) {
            player.openMenu(new SimpleMenuProvider((id, inventory, viewer) -> new SealGlobeMenu(id, inventory, pos),
                Component.translatable("item.thaumicadditions.seal_globe")), pos);
        }
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }
}
