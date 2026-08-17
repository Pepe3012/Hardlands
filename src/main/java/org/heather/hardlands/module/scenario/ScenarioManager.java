package org.heather.hardlands.module.scenario;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.heather.hardlands.Hardlands;
import org.heather.hardlands.core.data.json.JsonConvertible;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class ScenarioManager implements JsonConvertible {

    private final Map<String, Scenario> registeredScenarios = new LinkedHashMap<>();
    private final Set<String> activeScenarios = new LinkedHashSet<>();
    private final Hardlands plugin;

    public ScenarioManager(Hardlands plugin) {
        this.plugin = plugin;
        this.registerScenarios();
    }

    public boolean enableScenario(String identifier) {
        if (this.activeScenarios.contains(identifier)) return false;

        var scenario = this.registeredScenarios.get(identifier);
        if (scenario == null) return false;

        scenario.enable();
        this.activeScenarios.add(identifier);

        return true;
    }

    public boolean disableScenario(String identifier) {
        if (!this.activeScenarios.contains(identifier)) return false;

        var scenario = this.registeredScenarios.get(identifier);
        if (scenario == null) return false;

        scenario.disable();
        this.activeScenarios.remove(identifier);

        return true;
    }

    public boolean toggleScenario(String identifier) {
        return this.activeScenarios.contains(identifier)
                ? this.disableScenario(identifier)
                : this.enableScenario(identifier);
    }

    public Optional<Scenario> findRegisteredScenario(String identifier) {
        return Optional.ofNullable(this.registeredScenarios.get(identifier));
    }

    public Optional<Scenario> findActiveScenario(String identifier) {
        if (!this.activeScenarios.contains(identifier)) return Optional.empty();

        return this.findRegisteredScenario(identifier);
    }

    public List<Scenario> getScenarios() {
        return List.copyOf(this.registeredScenarios.values());
    }

    @Override
    public JsonElement toJson() {
        var json = new JsonObject();

        for (var identifier : this.activeScenarios) {
            json.add(identifier, this.registeredScenarios.get(identifier).toJson());
        }

        return json;
    }

    @Override
    public void fromJson(JsonElement json) {
        for (var identifier : Set.copyOf(this.activeScenarios)) {
            this.disableScenario(identifier);
        }

        for (var entry : json.getAsJsonObject().entrySet()) {
            var scenario = this.registeredScenarios.get(entry.getKey());
            if (scenario == null) continue;

            scenario.fromJson(entry.getValue());
            this.enableScenario(entry.getKey());
        }
    }

    private void registerScenarios() {
        for (var definition : ScenarioDefinition.values()) {
            var identifier = definition.getIdentifier();
            var scenario = definition.createScenario();

            scenario.initialize(this.plugin, identifier);

            if (this.registeredScenarios.putIfAbsent(identifier, scenario) != null) {
                throw new IllegalStateException("Scenario already registered: " + identifier);
            }
        }
    }
}