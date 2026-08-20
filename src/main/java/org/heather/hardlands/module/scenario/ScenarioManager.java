package org.heather.hardlands.module.scenario;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.heather.hardlands.Hardlands;
import org.heather.hardlands.core.data.json.JsonConvertible;

public final class ScenarioManager implements JsonConvertible {

    private final Hardlands plugin;
    private final Map<String, Scenario> scenarios = new LinkedHashMap<>();
    private final Set<String> enabledScenarioIds = new LinkedHashSet<>();

    public ScenarioManager(Hardlands plugin) {
        this.plugin = plugin;
        this.registerScenarios();
    }

    public boolean enableScenario(String identifier) {
        Scenario scenario = this.scenarios.get(identifier);

        if (scenario == null || this.enabledScenarioIds.contains(identifier)) return false;

        scenario.enable();
        this.enabledScenarioIds.add(identifier);

        return true;
    }

    public boolean disableScenario(String identifier) {
        Scenario scenario = this.scenarios.get(identifier);

        if (scenario == null || !this.enabledScenarioIds.contains(identifier)) return false;

        scenario.disable();
        this.enabledScenarioIds.remove(identifier);

        return true;
    }

    public boolean toggleScenario(String identifier) {
        return this.enabledScenarioIds.contains(identifier)
                ? this.disableScenario(identifier)
                : this.enableScenario(identifier);
    }

    public Optional<Scenario> findScenario(String identifier) {
        return Optional.ofNullable(this.scenarios.get(identifier));
    }

    public Optional<Scenario> findEnabledScenario(String identifier) {
        if (!this.enabledScenarioIds.contains(identifier)) return Optional.empty();
        return this.findScenario(identifier);
    }

    public List<Scenario> getScenarios() {
        return List.copyOf(this.scenarios.values());
    }

    public boolean isScenarioEnabled(String identifier) {
        return this.enabledScenarioIds.contains(identifier);
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();

        for (String identifier : this.enabledScenarioIds) {
            json.add(identifier, this.scenarios.get(identifier).toJson());
        }

        return json;
    }

    @Override
    public void fromJson(JsonElement json) {
        for (String identifier : List.copyOf(this.enabledScenarioIds)) {
            this.disableScenario(identifier);
        }

        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
            Scenario scenario = this.scenarios.get(entry.getKey());
            if (scenario == null) continue;

            scenario.fromJson(entry.getValue());
            this.enableScenario(entry.getKey());
        }
    }

    private void registerScenarios() {
        for (ScenarioDefinition definition : ScenarioDefinition.values()) {
            String identifier = definition.getIdentifier();
            Scenario scenario = definition.createScenario();

            scenario.initialize(this.plugin, identifier);

            if (this.scenarios.putIfAbsent(identifier, scenario) != null) {
                throw new IllegalStateException("Scenario is already registered: " + identifier);
            }
        }
    }
}
