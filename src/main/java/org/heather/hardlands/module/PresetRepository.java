package org.heather.hardlands.module;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import java.nio.file.Path;
import org.heather.hardlands.Hardlands;
import org.heather.hardlands.core.data.json.JsonDataManager;

public final class PresetRepository {

    private record Preset(
            @SerializedName("general") JsonObject general,
            @SerializedName("world") JsonObject world,
            @SerializedName("scenarios") JsonObject scenarios,
            @SerializedName("phase") JsonObject phase) {}

    private final Hardlands plugin;
    private final Path directory;

    private PresetRepository(Hardlands plugin, Path directory) {
        this.plugin = plugin;
        this.directory = directory;
    }

    public static PresetRepository create(Hardlands plugin) {
        return new PresetRepository(plugin, plugin.getDataPath().resolve("presets"));
    }

    public void save(String name) {
        this.managerFor(name).write(new Preset(
                this.plugin.getGeneralConfiguration().toJson().getAsJsonObject(),
                this.plugin.getWorldManagerOrThrow().toJson().getAsJsonObject(),
                this.plugin.getScenarioManager().toJson().getAsJsonObject(),
                this.plugin.getPhaseController().toJson().getAsJsonObject()));
    }

    public void load(String name) {
        this.managerFor(name).read().ifPresent(preset -> {
            this.plugin.getGeneralConfiguration().fromJson(preset.general());
            this.plugin.getWorldManagerOrThrow().fromJson(preset.world());
            this.plugin.getScenarioManager().fromJson(preset.scenarios());
            this.plugin.getPhaseController().fromJson(preset.phase());
        });
    }

    private JsonDataManager<Preset> managerFor(String name) {
        return new JsonDataManager<>(Hardlands.GSON, this.directory.resolve(name + ".json"), Preset.class);
    }
}
