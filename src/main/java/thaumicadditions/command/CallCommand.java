package thaumicadditions.command;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import thaumicadditions.ThaumicAdditions;

@EventBusSubscriber(modid = ThaumicAdditions.MOD_ID)
public final class CallCommand {
    private static final Map<MinecraftServer, Map<UUID, UUID>> REQUESTS = new WeakHashMap<>();

    private CallCommand() {
    }

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        var call = Commands.literal("call").then(Commands.argument("player", EntityArgument.player())
            .executes(context -> request(context.getSource().getPlayerOrException(), EntityArgument.getPlayer(context, "player")))
            .then(Commands.literal("+").executes(context -> respond(context.getSource().getPlayerOrException(), EntityArgument.getPlayer(context, "player"), true)))
            .then(Commands.literal("-").executes(context -> respond(context.getSource().getPlayerOrException(), EntityArgument.getPlayer(context, "player"), false))));
        event.getDispatcher().register(Commands.literal("thaumicadditions").then(call));
        if (event.getDispatcher().getRoot().getChild("call") == null) {
            event.getDispatcher().register(call);
        }
    }

    private static Map<UUID, UUID> pending(MinecraftServer server) {
        return REQUESTS.computeIfAbsent(server, ignored -> new HashMap<>());
    }

    private static int request(ServerPlayer sender, ServerPlayer recipient) throws CommandSyntaxException {
        if (sender == recipient) {
            throw new SimpleCommandExceptionType(Component.translatable("command.thaumicadditions.call_self")).create();
        }
        pending(sender.server).put(sender.getUUID(), recipient.getUUID());
        String accept = "/thaumicadditions call " + sender.getGameProfile().getName() + " +";
        String deny = "/thaumicadditions call " + sender.getGameProfile().getName() + " -";
        recipient.sendSystemMessage(Component.translatable("command.thaumicadditions.call_received", sender.getDisplayName())
            .append(Component.translatable("command.thaumicadditions.call_accept").withStyle(style ->
                style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, accept))))
            .append(Component.literal(" "))
            .append(Component.translatable("command.thaumicadditions.call_deny").withStyle(style ->
                style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, deny)))));
        sender.sendSystemMessage(Component.translatable("command.thaumicadditions.call_sent", recipient.getDisplayName()));
        return 1;
    }

    private static int respond(ServerPlayer recipient, ServerPlayer sender, boolean accept) throws CommandSyntaxException {
        Map<UUID, UUID> requests = pending(recipient.server);
        if (!recipient.getUUID().equals(requests.get(sender.getUUID()))) {
            throw new SimpleCommandExceptionType(Component.translatable("command.thaumicadditions.call_missing")).create();
        }
        requests.remove(sender.getUUID());
        if (accept) {
            sender.teleportTo(recipient.serverLevel(), recipient.getX(), recipient.getY(), recipient.getZ(), sender.getYRot(), sender.getXRot());
            sender.setDeltaMovement(Vec3.ZERO);
            sender.resetFallDistance();
        }
        String key = accept ? "command.thaumicadditions.call_accepted" : "command.thaumicadditions.call_denied";
        sender.sendSystemMessage(Component.translatable(key, recipient.getDisplayName()));
        recipient.sendSystemMessage(Component.translatable(key, sender.getDisplayName()));
        return 1;
    }

    @SubscribeEvent
    public static void logout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            Map<UUID, UUID> requests = REQUESTS.get(player.server);
            if (requests != null) {
                requests.entrySet().removeIf(entry -> entry.getKey().equals(player.getUUID()) || entry.getValue().equals(player.getUUID()));
            }
        }
    }

    @SubscribeEvent
    public static void stopped(ServerStoppedEvent event) {
        REQUESTS.remove(event.getServer());
    }
}
