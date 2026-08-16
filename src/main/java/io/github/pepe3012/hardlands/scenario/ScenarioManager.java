package io.github.pepe3012.hardlands.scenario;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.pepe3012.hardlands.Hardlands;
import io.github.pepe3012.hardlands.data.json.JsonConvertible;
import org.bukkit.plugin.Plugin;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class ScenarioManager implements JsonConvertible {

    private final Map<ScenarioDefinition, ScenarioModule> scenarios = new EnumMap<>(ScenarioDefinition.class);
    private final Set<ScenarioDefinition> activeScenarios = EnumSet.noneOf(ScenarioDefinition.class);
    private final Plugin plugin;

    public ScenarioManager(Plugin plugin) {
        this.register(ScenarioDefinition.values());
        this.plugin = plugin;
    }

    public void register(ScenarioDefinition... definitions) {
        for (ScenarioDefinition definition : definitions) {
            if (this.scenarios.putIfAbsent(definition, definition.createModule()) != null) {
                throw new IllegalArgumentException("Scenario already registered: " + definition);
            }
        }
    }

    public boolean enable(ScenarioDefinition definition) {
        if (this.activeScenarios.contains(definition)) return false;

        this.get(definition).enable(this.plugin);
        this.activeScenarios.add(definition);

        return true;
    }

    public boolean disable(ScenarioDefinition definition) {
        if (!this.activeScenarios.contains(definition)) return false;

        try {
            this.get(definition).disable();
        } finally {
            this.activeScenarios.remove(definition);
        }

        return true;
    }

    public boolean toggle(ScenarioDefinition definition) {
        return this.isActive(definition)
                ? this.disable(definition)
                : this.enable(definition);
    }

    public boolean isRegistered(ScenarioDefinition definition) {
        return this.scenarios.containsKey(definition);
    }

    public boolean isActive(ScenarioDefinition definition) {
        return this.activeScenarios.contains(definition);
    }

    public ScenarioModule get(ScenarioDefinition definition) {
        ScenarioModule scenario = this.scenarios.get(definition);

        if (scenario == null) {
            throw new IllegalArgumentException("Scenario is not registered: " + definition);
        }

        return scenario;
    }

    public Optional<ScenarioModule> findActive(ScenarioDefinition definition) {
        if (!this.activeScenarios.contains(definition)) return Optional.empty();

        return Optional.of(this.get(definition));
    }

    public List<ScenarioModule> getScenarios() {
        return List.copyOf(this.scenarios.values());
    }

    public List<ScenarioModule> getActiveScenarios() {
        return this.activeScenarios.stream()
                .map(this::get)
                .toList();
    }

    public List<ScenarioDefinition> getDefinitions() {
        return List.copyOf(this.scenarios.keySet());
    }

    public List<ScenarioDefinition> getActiveDefinitions() {
        return List.copyOf(this.activeScenarios);
    }

    @Override
    public String toJson() {
        JsonObject json = new JsonObject();
        this.scenarios.values().forEach(scenario ->
                json.add(scenario.getIdentifier(), JsonParser.parseString(scenario.toJson())));
        return Hardlands.GSON.toJson(json);
    }

    @Override
    public void fromJson(String json) {
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        this.scenarios.values().forEach(scenario ->
                scenario.fromJson(root.get(scenario.getIdentifier()).toString()));
    }
}