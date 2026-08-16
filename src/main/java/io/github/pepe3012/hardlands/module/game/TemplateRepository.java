package io.github.pepe3012.hardlands.module.game;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import io.github.pepe3012.hardlands.Hardlands;
import io.github.pepe3012.hardlands.core.data.json.JsonStore;

import java.nio.file.Path;

public final class TemplateRepository {

    private final Hardlands plugin;
    private final Path directory;

    public TemplateRepository(Hardlands plugin, Path directory) {
        this.plugin = plugin;
        this.directory = directory;
    }

    public void save(String name) {
        this.jsonStore(name).write(new Snapshot(
                this.plugin.getWorldManager().toJson().getAsJsonObject(),
                this.plugin.getScenarioManager().toJson().getAsJsonObject()
        ));
    }

    public void load(String name) {
        this.jsonStore(name).read().ifPresent(snapshot -> {
            this.plugin.getWorldManager().fromJson(snapshot.world());
            this.plugin.getScenarioManager().fromJson(snapshot.scenarios());
        });
    }

    private JsonStore<Snapshot> jsonStore(String name) {
        return new JsonStore<>(
                Hardlands.GSON,
                this.directory.resolve(name + ".json"),
                Snapshot.class
        );
    }

    private record Snapshot(
            @SerializedName("world") JsonObject world,
            @SerializedName("scenarios") JsonObject scenarios
    ) {}
}