package dev.aresiel.respawn_nearby.networking;

import dev.aresiel.respawn_nearby.RespawnNearby;
import dev.aresiel.respawn_nearby.singletons.RespawnPreferencesSingleton;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public class UpdateStoredPreferencePayloadHandler implements IPayloadHandler<UpdateStoredPreferencePayload> {

    @Override
    public void handle(UpdateStoredPreferencePayload updateStoredPreferencePayload, IPayloadContext iPayloadContext) {
        var player = iPayloadContext.player();
        var preference = updateStoredPreferencePayload.respawnNearby();

        RespawnPreferencesSingleton.setRespawnPreference(player.getUUID(), preference);
    }
}
