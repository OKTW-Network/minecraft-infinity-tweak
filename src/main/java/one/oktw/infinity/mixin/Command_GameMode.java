package one.oktw.infinity.mixin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.GameModeCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;
import java.util.Collections;

@Mixin(GameModeCommand.class)
public abstract class Command_GameMode {
    @Shadow
    private static int execute(CommandContext<ServerCommandSource> context, Collection<ServerPlayerEntity> targets, GameMode gameMode) {
        return 0;
    }

    /**
     * @author jamess58899
     * @reason Disable GameMode Permission Check
     */
    @Overwrite
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("gamemode");

        for (GameMode gameMode : GameMode.values()) {
            if (gameMode != GameMode.NOT_SET) {
                literalArgumentBuilder
                        .then(CommandManager.literal(gameMode.getName())
                                .executes((commandContext) -> execute(commandContext, Collections.singleton(commandContext.getSource().getPlayer()), gameMode))
                                .then(CommandManager.argument("target", EntityArgumentType.players())
                                        .executes((commandContext) -> execute(commandContext, EntityArgumentType.getPlayers(commandContext, "target"), gameMode))
                                        .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))));
            }
        }

        dispatcher.register(literalArgumentBuilder);
    }
}
