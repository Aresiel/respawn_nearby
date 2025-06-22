package dev.aresiel.respawn_nearby.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLE_REPAWN_NEARBY = BUILDER
            .comment("Whether or not to respawn nearby your last death location. This can be toggled from the death screen.")
            .define("enable_respawn_nearby", false);

    public static final ModConfigSpec SPEC = BUILDER.build();
}
