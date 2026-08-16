package io.github.pepe3012.hardlands.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import io.github.pepe3012.hardlands.Hardlands;
import io.github.pepe3012.hardlands.data.json.JsonDataStore;

public record HardlandsData(
        @SerializedName("world") JsonObject world,
        @SerializedName("scenarios") JsonObject scenarios
) {

    private void apply(Hardlands plugin) {
        plugin.getWorldManager().deserializeOptions(this.world.toString());
        plugin.getScenarioManager().fromJson(this.scenarios.toString());
    }

    public static void save(Hardlands plugin) {
        store(plugin).write(capture(plugin));
    }

    public static void load(Hardlands plugin) {
        store(plugin).read().ifPresent(data -> data.apply(plugin));
    }

    private static HardlandsData capture(Hardlands plugin) {
        return new HardlandsData(
                JsonParser.parseString(plugin.getWorldManager().serializeOptions()).getAsJsonObject(),
                JsonParser.parseString(plugin.getScenarioManager().toJson()).getAsJsonObject()
        );
    }

    private static JsonDataStore<HardlandsData> store(Hardlands plugin) {
        return new JsonDataStore<>(Hardlands.GSON, plugin.getDataPath().resolve("data.json"), HardlandsData.class);
    }
}