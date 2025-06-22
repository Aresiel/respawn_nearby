package dev.aresiel.respawn_nearby.networking;

import dev.aresiel.respawn_nearby.RespawnNearby;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record UpdateStoredPreferencePayload(boolean respawnNearby) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpdateStoredPreferencePayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RespawnNearby.MODID, "update_respawn_nearby_preference"));

    public static final StreamCodec<ByteBuf, UpdateStoredPreferencePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            UpdateStoredPreferencePayload::respawnNearby,
            UpdateStoredPreferencePayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type(){
        return TYPE;
    }
}
