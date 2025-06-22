package dev.aresiel.respawn_nearby;

import dev.aresiel.respawn_nearby.config.ClientConfig;
import dev.aresiel.respawn_nearby.config.ServerConfig;
import dev.aresiel.respawn_nearby.networking.UpdateStoredPreferencePayload;
import dev.aresiel.respawn_nearby.networking.UpdateStoredPreferencePayloadHandler;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(RespawnNearby.MODID)
public class RespawnNearby {
    public static final String MODID = "respawn_nearby";

    public static final Logger LOGGER = LogUtils.getLogger();

    public RespawnNearby(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::registerPayloadHandlers);

        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
    }

    public void registerPayloadHandlers(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.commonToServer(
                UpdateStoredPreferencePayload.TYPE,
                UpdateStoredPreferencePayload.STREAM_CODEC,
                new UpdateStoredPreferencePayloadHandler()
        );
    }
}
