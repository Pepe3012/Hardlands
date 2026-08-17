package org.heather.hardlands.module;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.heather.hardlands.Hardlands;
import org.heather.hardlands.core.data.json.JsonDataManager;

import java.nio.file.Path;

public final class PresetRepository {

    private record Preset(
            @SerializedName("world") JsonObject world,
            @SerializedName("scenarios") JsonObject scenarios//,
            //@SerializedName("timer") JsonObject timer
    ) {}

    private final Hardlands plugin;
    private final Path directory;

    private PresetRepository(Hardlands plugin, Path directory) {
        this.plugin = plugin;
        this.directory = directory;
    }

    public static PresetRepository create(Hardlands plugin) {
        return new PresetRepository(plugin, plugin.getDataPath().resolve("templates"));
    }

    public void save(String name) {
        managerFor(name).write(new Preset(
                plugin.getWorldManager().toJson().getAsJsonObject(),
                plugin.getScenarioManager().toJson().getAsJsonObject()
        ));
    }

    public void load(String name) {
        managerFor(name).read().ifPresent(preset -> {
            plugin.getWorldManager().fromJson(preset.world());
            plugin.getScenarioManager().fromJson(preset.scenarios());
        });
    }

    private JsonDataManager<Preset> managerFor(String name) {
        return new JsonDataManager<>(
                Hardlands.GSON,
                this.directory.resolve(name + ".json"),
                Preset.class
        );
    }
}