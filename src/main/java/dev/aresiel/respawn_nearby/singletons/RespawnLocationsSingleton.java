package dev.aresiel.respawn_nearby.singletons;

import dev.aresiel.respawn_nearby.RespawnNearby;
import dev.aresiel.respawn_nearby.config.ServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.*;

@EventBusSubscriber(modid = RespawnNearby.MODID)
public class RespawnLocationsSingleton {

    private static Map<UUID, List<Location>> respawnLocations = new HashMap<>();
    private static final Map<UUID, Integer> updateCooldowns = new HashMap<>();


    @SubscribeEvent
    public static void onPlayerPostTick(PlayerTickEvent.Post event) {
        var player = event.getEntity();

        if(player.level().isClientSide()) {
            return;
        }

        updateCooldowns.putIfAbsent(player.getUUID(), 0);

        if(updateCooldowns.get(player.getUUID()) <= 0 && player.onGround() && player.isAlive()) {
            updateCooldowns.put(player.getUUID(), ServerConfig.UPDATE_COOLDOWN_TICKS.get());

            respawnLocations.putIfAbsent(player.getUUID(), new ArrayList<>());
            var respawnLocation = respawnLocations.get(player.getUUID());

            if(respawnLocation.size() >= ServerConfig.NUMBER_OF_RESPAWN_LOCATIONS_TO_STORE.get()) {
                respawnLocation.remove(0); // Remove the oldest location if we reach the limit
            }

            respawnLocation.add(new Location(
                    player.level().dimension(),
                    BlockPos.containing(player.getPosition(0))
            ));
        }

        if(player.isAlive()) {
            updateCooldowns.merge(player.getUUID(), -1, Integer::sum);
        }
    }


    public static Optional<Location> getBestRespawnLocation(Player player, ServerLevel world) {
        List<Location> locations = getRespawnLocations(player).reversed(); // Reverse to get the most recent first

        if (locations.isEmpty()) {
            return Optional.empty();
        }

        return locations.stream()
                .max(Comparator.comparingDouble(location -> getDistanceToNearestEnemy(location.pos().getBottomCenter(), world)));
    }

    private static double getDistanceToNearestEnemy(Vec3 location, ServerLevel world) {
        int search_radius = ServerConfig.ENTITY_SEARCH_RADIUS.get();

        return world.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(location, search_radius*2, search_radius*2, search_radius*2))
                .stream()
                .filter(LivingEntity::isAlive)
                .mapToDouble(entity -> entity.position().distanceTo(location))
                .min()
                .orElse(Double.MAX_VALUE);
    }

    /**
     * Get the respawn locations for a player, ordered oldest->newest.
     */
    public static List<Location> getRespawnLocations(Player player) {
        var locations = respawnLocations.getOrDefault(player.getUUID(), new ArrayList<>());
        /*locations.addFirst(new Location( // Do we want this?
                player.level().dimension(),
                BlockPos.containing(player.getPosition(0))
        ));*/
        return locations;
    }

    /**
     * Clear the respawn locations and cooldowns for a player.
     */
    public static void clearRespawnLocations(Player player) {
        respawnLocations.remove(player.getUUID());
        updateCooldowns.remove(player.getUUID());
    }

    public record Location(ResourceKey<Level> dimension, BlockPos pos) {}
}
