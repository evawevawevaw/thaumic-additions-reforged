package thaumicadditions.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import thaumcraft.platform.BiomeEdit;
import thaumicadditions.ThaumicAdditions;
import thaumicadditions.entity.SingularityEntity;
import thaumicadditions.world.MonolithStructure;

@EventBusSubscriber(modid = ThaumicAdditions.MOD_ID)
public final class PortCommands {
    private PortCommands() {
    }

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        var spawn = Commands.literal("spawn").requires(source -> source.hasPermission(3))
            .then(Commands.literal("monolith").executes(context -> {
                var source = context.getSource();
                BlockPos pos = BlockPos.containing(source.getPosition()).below();
                if (!MonolithStructure.apply(source.getLevel(), MonolithStructure.plan(pos, false, true), null, true)) {
                    throw failure("command.thaumicadditions.structure_failed");
                }
                source.sendSuccess(() -> Component.translatable("command.thaumicadditions.monolith_created"), true);
                return 1;
            }))
            .then(Commands.literal("singularity").executes(context -> spawnSingularity(context.getSource(), context.getSource().getPlayerOrException()))
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(context -> spawnSingularity(context.getSource(), EntityArgument.getPlayer(context, "player")))));
        var biome = Commands.literal("setbiome").requires(source -> source.hasPermission(3))
            .then(Commands.argument("biome", ResourceArgument.resource(event.getBuildContext(), Registries.BIOME))
                .executes(context -> biome(context, 25))
                .then(Commands.argument("radius", IntegerArgumentType.integer(0))
                    .executes(context -> biome(context, IntegerArgumentType.getInteger(context, "radius")))));
        var structure = Commands.literal("structure").requires(source -> source.hasPermission(3))
            .then(Commands.literal("write").then(Commands.argument("name", StringArgumentType.word())
                .then(Commands.argument("from", BlockPosArgument.blockPos())
                    .then(Commands.argument("to", BlockPosArgument.blockPos()).executes(PortCommands::write)))))
            .then(Commands.literal("build").then(Commands.argument("name", StringArgumentType.word()).executes(PortCommands::build)
                .then(Commands.argument("pos", BlockPosArgument.blockPos()).executes(PortCommands::build))));
        var root = event.getDispatcher().register(Commands.literal("thaumicadditions").then(spawn).then(biome).then(structure));
        alias(event, "spawn", root.getChild("spawn"));
        alias(event, "xu_setbiome", root.getChild("setbiome"));
        alias(event, "ta_writestructure", root.getChild("structure"));
    }

    private static void alias(RegisterCommandsEvent event, String name, com.mojang.brigadier.tree.CommandNode<CommandSourceStack> target) {
        if (event.getDispatcher().getRoot().getChild(name) == null) {
            event.getDispatcher().register(Commands.literal(name).requires(source -> source.hasPermission(3)).redirect(target));
        }
    }

    private static int spawnSingularity(CommandSourceStack source, net.minecraft.server.level.ServerPlayer target) {
        SingularityEntity entity = SingularityEntity.thrown(target.level(), target);
        return target.level().addFreshEntity(entity) ? 1 : 0;
    }

    private static int biome(CommandContext<CommandSourceStack> context, int radius) throws CommandSyntaxException {
        var source = context.getSource();
        int limit = source.getLevel().getGameRules().getInt(GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT);
        if (radius > (Math.sqrt(limit) - 1) / 2) {
            throw failure("command.thaumicadditions.region_too_large");
        }
        BlockPos center = BlockPos.containing(source.getPosition());
        BlockPos min = center.offset(-radius, 0, -radius);
        BlockPos max = center.offset(radius, 0, radius);
        region(source.getLevel(), min, max);
        var biome = ResourceArgument.getResource(context, "biome", Registries.BIOME);
        int changed = 0;
        for (int x = QuartPos.fromBlock(min.getX()); x <= QuartPos.fromBlock(max.getX()); x++) {
            for (int z = QuartPos.fromBlock(min.getZ()); z <= QuartPos.fromBlock(max.getZ()); z++) {
                if (BiomeEdit.setColumn(source.getLevel(), new BlockPos(QuartPos.toBlock(x), center.getY(), QuartPos.toBlock(z)), biome)) {
                    changed++;
                }
            }
        }
        int result = changed;
        source.sendSuccess(() -> Component.translatable("command.thaumicadditions.biome_changed", result), true);
        return changed;
    }

    private static ResourceLocation structureId(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String name = StringArgumentType.getString(context, "name");
        if (!name.matches("[a-z0-9_-]+")) {
            throw failure("command.thaumicadditions.invalid_structure_name");
        }
        return ResourceLocation.fromNamespaceAndPath("thaumicadditions", "exports/" + name);
    }

    private static void region(ServerLevel level, BlockPos min, BlockPos max) throws CommandSyntaxException {
        long volume = (long)(max.getX() - min.getX() + 1) * (max.getY() - min.getY() + 1) * (max.getZ() - min.getZ() + 1);
        if (volume > level.getGameRules().getInt(GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT)
            || volume <= 0 || level.isOutsideBuildHeight(min) || level.isOutsideBuildHeight(max)
            || !level.getWorldBorder().isWithinBounds(min) || !level.getWorldBorder().isWithinBounds(max)) {
            throw failure("command.thaumicadditions.region_too_large");
        }
        for (int x = min.getX() >> 4; x <= max.getX() >> 4; x++) {
            for (int z = min.getZ() >> 4; z <= max.getZ() >> 4; z++) {
                if (!level.hasChunk(x, z)) {
                    throw failure("argument.pos.unloaded");
                }
            }
        }
    }

    private static int write(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var source = context.getSource();
        BlockPos from = BlockPosArgument.getLoadedBlockPos(context, "from");
        BlockPos to = BlockPosArgument.getLoadedBlockPos(context, "to");
        BlockPos min = new BlockPos(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
        BlockPos max = new BlockPos(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
        region(source.getLevel(), min, max);
        ResourceLocation id = structureId(context);
        var manager = source.getLevel().getStructureManager();
        var template = manager.getOrCreate(id);
        Vec3i size = max.subtract(min).offset(1, 1, 1);
        template.fillFromWorld(source.getLevel(), min, size, false, Blocks.STRUCTURE_VOID);
        if (!manager.save(id)) {
            throw failure("command.thaumicadditions.structure_failed");
        }
        source.sendSuccess(() -> Component.translatable("command.thaumicadditions.structure_saved", id.toString()), true);
        return 1;
    }

    private static int build(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var source = context.getSource();
        ResourceLocation id = structureId(context);
        var template = source.getLevel().getStructureManager().get(id);
        if (template.isEmpty()) {
            throw failure("command.thaumicadditions.structure_missing");
        }
        BlockPos pos;
        try {
            pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
        } catch (IllegalArgumentException missing) {
            pos = BlockPos.containing(source.getPosition());
        }
        var size = template.get().getSize();
        region(source.getLevel(), pos, pos.offset(size).offset(-1, -1, -1));
        if (!template.get().placeInWorld(source.getLevel(), pos, pos, new StructurePlaceSettings().setIgnoreEntities(true),
            source.getLevel().random, Block.UPDATE_ALL)) {
            throw failure("command.thaumicadditions.structure_failed");
        }
        source.sendSuccess(() -> Component.translatable("command.thaumicadditions.structure_built", id.toString()), true);
        return 1;
    }

    private static CommandSyntaxException failure(String key) {
        return new SimpleCommandExceptionType(Component.translatable(key)).create();
    }
}
