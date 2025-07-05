package dev.aresiel.respawn_nearby.config;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue NUMBER_OF_RESPAWN_LOCATIONS_TO_STORE = BUILDER
            .comment("How many nearby respawn locations to store for each player.")
            .defineInRange("stored_locations_amount", 20, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue UPDATE_COOLDOWN_TICKS = BUILDER
            .comment("Cooldown in ticks for adding respawn locations. Default is 10 seconds (200 ticks).")
            .defineInRange("update_cooldown_ticks", 200, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue ENTITY_SEARCH_RADIUS = BUILDER
            .comment("The algorithm picks the location with the fewest nearby entities, this determines the search radius to look for entities in.")
            .defineInRange("entity_search_radius", 20, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.BooleanValue USE_ROTATING_ARRAY_LIST = BUILDER
            .comment("Use RotatingArrayList for storing respawn locations. This might improve performance for very large numbers of locations and players. It however has not been tested for stability, nor has any performance profiling been done, for these reasons, usage is not supported, do not submit crash reports with it enabled.")
            .define("use_rotating_array_list", false);

    public static final ModConfigSpec SPEC = BUILDER.build();
}
