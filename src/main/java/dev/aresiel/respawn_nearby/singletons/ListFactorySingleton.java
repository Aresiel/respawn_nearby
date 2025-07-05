package dev.aresiel.respawn_nearby.singletons;

import dev.aresiel.respawn_nearby.config.ServerConfig;
import io.github.nopeless.project2.RotatingArrayList;

import java.util.ArrayList;
import java.util.List;

public class ListFactorySingleton {
    public static <T> List<T> createList() {
        return ServerConfig.USE_ROTATING_ARRAY_LIST.get()
                ? new RotatingArrayList<>()
                : new ArrayList<>();
    }
}
