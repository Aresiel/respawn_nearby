package dev.aresiel.respawn_nearby.events;

import dev.aresiel.respawn_nearby.RespawnNearby;
import dev.aresiel.respawn_nearby.config.ClientConfig;
import dev.aresiel.respawn_nearby.networking.UpdateStoredPreferencePayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = "respawn_nearby")
public class SendPreferenceOnJoin {
    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Player && event.getEntity().level().isClientSide()) {
            PacketDistributor.sendToServer(new UpdateStoredPreferencePayload(ClientConfig.ENABLE_REPAWN_NEARBY.get()));
        }
    }
}
