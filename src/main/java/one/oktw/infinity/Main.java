package one.oktw.infinity;

import com.mojang.brigadier.Command;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

import java.util.EnumSet;

@SuppressWarnings("unused")
public class Main implements ModInitializer {
    @Override
    public void onInitialize() {
        System.out.println("\n" +
                " _____ _   _ ______ _____ _   _ _____ _________     __\n" +
                "|_   _| \\ | |  ____|_   _| \\ | |_   _|__   __\\ \\   / /\n" +
                "  | | |  \\| | |__    | | |  \\| | | |    | |   \\ \\_/ / \n" +
                "  | | | . ` |  __|   | | | . ` | | |    | |    \\   /  \n" +
                " _| |_| |\\  | |     _| |_| |\\  |_| |_   | |     | |   \n" +
                "|_____|_| \\_|_|    |_____|_| \\_|_____|  |_|     |_|   \n"
        );

        CommandRegistry.INSTANCE.register(false, dispatcher -> dispatcher.register(
                CommandManager
                        .literal("tpx")
                        .then(CommandManager.argument("target", EntityArgumentType.player()).executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "target");
                            ServerWorld world = player.getServerWorld();

                            double x, y, z;
                            float yaw, pitch;
                            x = target.getX();
                            y = target.getY();
                            z = target.getZ();
                            yaw = target.yaw;
                            pitch = target.pitch;

                            player.stopRiding();
                            if (player.isSleeping()) {
                                player.wakeUp(true, true);
                            }

                            if (world == target.world) {
                                player.networkHandler.teleportRequest(x, y, z, yaw, pitch, EnumSet.noneOf(PlayerPositionLookS2CPacket.Flag.class));
                            } else {
                                player.teleport(target.getServerWorld(), x, y, z, yaw, pitch);
                            }

                            player.setHeadYaw(yaw);
                            return Command.SINGLE_SUCCESS;
                        }))
        ));

        CommandRegistry.INSTANCE.register(false, dispatcher -> dispatcher.register(
                CommandManager.literal("spawn").executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    ServerWorld world = player.server.getWorld(DimensionType.OVERWORLD);
                    BlockPos spawnPos = world.getSpawnPos();

                    player.stopRiding();
                    if (player.isSleeping()) {
                        player.wakeUp(true, true);
                    }

                    if (world == player.world) {
                        player.networkHandler.teleportRequest(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), player.yaw, player.pitch, EnumSet.noneOf(PlayerPositionLookS2CPacket.Flag.class));
                    } else {
                        player.teleport(world, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), player.yaw, player.pitch);
                    }

                    return Command.SINGLE_SUCCESS;
                })
        ));
    }
}
