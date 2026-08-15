package io.github.pepe3012.hardlands.data;

import com.google.gson.JsonObject;
import io.github.pepe3012.hardlands.Hardlands;

import java.util.Map;

public record HardlandsData(
        JsonObject world,
        Map<String, JsonObject> scenarios
) {

    private static final String FILE_NAME = "data.json";

    public static void serialize(Hardlands plugin) {
        new JsonDataStore<>(plugin.getDataPath().resolve(FILE_NAME), HardlandsData.class).write(from(plugin));
    }

    public static void deserialize(Hardlands plugin) {
        new JsonDataStore<>(plugin.getDataPath().resolve(FILE_NAME), HardlandsData.class)
                .read()
                .ifPresent(data -> data.apply(plugin));
    }

    private static HardlandsData from(Hardlands plugin) {
        return new HardlandsData(
                Hardlands.GSON.fromJson(plugin.getWorldManager().serializeOptions(), JsonObject.class),
                plugin.getScenarioManager().serializeOptions()
        );
    }

    private void apply(Hardlands plugin) {
        plugin.getWorldManager().deserializeOptions(Hardlands.GSON.toJson(this.world));
        plugin.getScenarioManager().deserializeOptions(this.scenarios);
    }
}