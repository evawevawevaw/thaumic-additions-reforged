package thaumicadditions.item;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import thaumicadditions.advancement.ModAdvancements;
import thaumicadditions.registry.ModDataComponents;

public final class ArcaneTransporterItem extends Item {
    public ArcaneTransporterItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.has(ModDataComponents.TRANSPORTED_BLOCK.get());
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Player player = context.getPlayer();
        TransportedBlock cargo = stack.get(ModDataComponents.TRANSPORTED_BLOCK.get());
        BlockPos pos = cargo == null ? context.getClickedPos() : context.getClickedPos().relative(context.getClickedFace());
        Level level = context.getLevel();
        if (player == null || player.isSpectator() || !level.isInWorldBounds(pos) || !level.isLoaded(pos)
            || !level.getWorldBorder().isWithinBounds(pos) || !level.mayInteract(player, pos)
            || !player.mayUseItemAt(pos, context.getClickedFace(), stack) || stack.getCount() != 1) {
            return InteractionResult.FAIL;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!(level instanceof ServerLevel server) || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.FAIL;
        }
        boolean success = cargo == null ? BlockTransport.capture(server, pos, serverPlayer, stack) :
            BlockTransport.place(server, pos, context.getClickedFace(), serverPlayer, stack, cargo);
        return success ? InteractionResult.CONSUME : InteractionResult.FAIL;
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            ModAdvancements.award(serverPlayer, "arcane_transporter");
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        TransportedBlock cargo = stack.get(ModDataComponents.TRANSPORTED_BLOCK.get());
        if (cargo == null) {
            return;
        }
        tooltip.add(cargo.state().getBlock().getName());
        if (flag.isAdvanced()) {
            tooltip.add(Component.literal(BuiltInRegistries.BLOCK.getKey(cargo.state().getBlock()).toString()));
            tooltip.add(Component.literal(cargo.state().getValues().toString()));
        }
    }
}
