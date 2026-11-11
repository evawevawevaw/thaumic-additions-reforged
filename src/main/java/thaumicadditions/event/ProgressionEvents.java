package thaumicadditions.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import thaumcraft.item.ScribingToolsItem;
import thaumcraft.registry.EldritchContent;
import thaumcraft.wand.Wands;
import thaumicadditions.ThaumicAdditions;
import thaumicadditions.advancement.ModAdvancements;
import thaumicadditions.registry.ModItems;

@EventBusSubscriber(modid = ThaumicAdditions.MOD_ID)
public final class ProgressionEvents {
    private ProgressionEvents() {
    }

    @SubscribeEvent
    public static void crafted(PlayerEvent.ItemCraftedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        ItemStack stack = event.getCrafting();
        if (stack.is(ModItems.ARCANE_STONE.get()) || stack.is(ModItems.ARCANE_DICE.get())) {
            player.serverLevel().addFreshEntity(new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(),
                new ItemStack(EldritchContent.PRIMORDIAL_PEARL.get())));
        }
        if (Wands.isWand(stack)) {
            String rod = Wands.getRod(stack).tag();
            if (rod.equalsIgnoreCase("wood")) {
                ModAdvancements.award(player, "basicwand");
            }
            if (rod.equalsIgnoreCase("greatwood")) {
                ModAdvancements.award(player, "greatwoodwand");
            }
            if (rod.equalsIgnoreCase("silverwood")) {
                ModAdvancements.award(player, "silverwoodwand");
            }
            if (Wands.getCap(stack).tag().equalsIgnoreCase("void")) {
                ModAdvancements.award(player, "voidwand");
            }
        }
        if (stack.getItem() instanceof ScribingToolsItem) {
            ModAdvancements.award(player, "research");
        }
    }

    @SubscribeEvent
    public static void pickedUp(ItemEntityPickupEvent.Post event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }
        ItemStack stack = event.getOriginalStack();
        if (stack.is(EldritchContent.PRIMORDIAL_PEARL.get())) {
            ModAdvancements.award(player, "primalpearl");
        }
        if (stack.is(thaumcraft.registry.ModItems.ALUMENTUM.get())) {
            ModAdvancements.award(player, "alumentum");
        }
        if (stack.is(thaumcraft.registry.ModItems.NITOR.get())) {
            ModAdvancements.award(player, "nitor");
        }
        if (stack.is(thaumcraft.registry.ModItems.THAUMIUM_INGOT.get())) {
            ModAdvancements.award(player, "thaumium");
        }
        if (stack.is(ModItems.ARCANE_TRANSPORTER.get())) {
            ModAdvancements.award(player, "arcane_transporter");
        }
    }

    @SubscribeEvent
    public static void alchemy(PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntity() instanceof ServerPlayer player && Wands.isWand(event.getItemStack())
            && event.getLevel().getBlockState(event.getPos()).is(Blocks.CAULDRON)) {
            ModAdvancements.award(player, "alchemy");
        }
    }
}
