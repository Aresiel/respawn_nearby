package dev.aresiel.respawn_nearby.singletons;

import java.util.Map;
import java.util.UUID;

public class RespawnPreferencesSingleton {
    private static Map<UUID, Boolean> respawnPreferences = new java.util.HashMap<>();

    public static boolean getRespawnPreference(UUID playerUUID) {
        return respawnPreferences.getOrDefault(playerUUID, false);
    }

    public static void setRespawnPreference(UUID playerUUID, boolean respawnNearby) {
        respawnPreferences.put(playerUUID, respawnNearby);
    }
}
