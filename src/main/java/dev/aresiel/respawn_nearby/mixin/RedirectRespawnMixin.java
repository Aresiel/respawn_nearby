package dev.aresiel.respawn_nearby.mixin;

import dev.aresiel.respawn_nearby.RespawnNearby;
import dev.aresiel.respawn_nearby.helpers.RespawnData;
import dev.aresiel.respawn_nearby.singletons.RespawnLocationsSingleton;
import dev.aresiel.respawn_nearby.singletons.RespawnPreferencesSingleton;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.swing.text.html.parser.Entity;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(PlayerList.class)
public class RedirectRespawnMixin {
    @Unique
    private static final Map<UUID, RespawnData> respawnDataCache = new HashMap<>();

    @Inject(method = "respawn", at = @At("HEAD"))
    private void onRespawnHead(ServerPlayer player, boolean keepInventory, net.minecraft.world.entity.Entity.RemovalReason reason, CallbackInfoReturnable<ServerPlayer> cir) {
        if(respawnDataCache.containsKey(player.getUUID())) {
            throw new IllegalStateException("RespawnData cache was not cleared before respawn!");
        }

        if(!RespawnPreferencesSingleton.getRespawnPreference(player.getUUID())) {
            return;
        }

        var bestRespawnLocation = RespawnLocationsSingleton.getBestRespawnLocation(player, (ServerLevel) player.level());
        if(bestRespawnLocation.isPresent()) {
            respawnDataCache.put(player.getUUID(), RespawnData.fromPlayer(player));
            player.setRespawnPosition(
                    bestRespawnLocation.get().dimension(),
                    bestRespawnLocation.get().pos(),
                    player.getRespawnAngle(),
                    true,
                    false
            );
        }
    }

    @Inject(method = "respawn", at = @At("RETURN"))
    private void onRespawnReturn(ServerPlayer player, boolean keepInventory, net.minecraft.world.entity.Entity.RemovalReason reason, CallbackInfoReturnable<ServerPlayer> cir) {
        var new_player = cir.getReturnValue();

        if(respawnDataCache.containsKey(player.getUUID())) {
            RespawnData respawnData = respawnDataCache.remove(player.getUUID());
            respawnData.applyToPlayer(new_player, false);
        }
    }


}
