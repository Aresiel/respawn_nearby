package dev.aresiel.respawn_nearby.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public record RespawnData(BlockPos position, ResourceKey<Level> dimension, float angle, boolean forced) {
    public static RespawnData fromPlayer(ServerPlayer player) {
        return new RespawnData(
                player.getRespawnPosition(),
                player.getRespawnDimension(),
                player.getRespawnAngle(),
                player.isRespawnForced()
        );
    }

    public void applyToPlayer(ServerPlayer player, boolean sendMessage) {
        player.setRespawnPosition(dimension(), position(), angle(), forced(), sendMessage);
    }
}
