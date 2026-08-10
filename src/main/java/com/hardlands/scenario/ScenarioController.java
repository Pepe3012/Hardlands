package com.hardlands.scenario;

import org.bukkit.plugin.Plugin;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ScenarioController {

    private final Map<ScenarioDefinition, ScenarioModule> registeredScenarios = new EnumMap<>(ScenarioDefinition.class);
    private final Map<ScenarioDefinition, ScenarioModule> activeScenarios = new EnumMap<>(ScenarioDefinition.class);

    private final Plugin plugin;

    public ScenarioController(final Plugin plugin) {
        this.plugin = plugin;
    }

    public void registerScenarios(ScenarioDefinition... scenarioDefinitions) {
        for (ScenarioDefinition scenarioDefinition : scenarioDefinitions) {
            if (registeredScenarios.containsKey(scenarioDefinition)) {
                throw new IllegalArgumentException("Scenario is already registered: " + scenarioDefinition);
            }
            registeredScenarios.put(scenarioDefinition, scenarioDefinition.createModule());
        }
    }

    public boolean enableScenario(ScenarioDefinition scenarioDefinition) {
        if (isScenarioActive(scenarioDefinition)) return false;

        ScenarioModule scenarioModule = this.getRegisteredScenario(scenarioDefinition);
        scenarioModule.enable(plugin);
        activeScenarios.put(scenarioDefinition, scenarioModule);

        return true;
    }

    public boolean disableScenario(ScenarioDefinition scenarioDefinition) {
        ScenarioModule scenarioModule = activeScenarios.get(scenarioDefinition);
        if (scenarioModule == null) return false;

        try {
            scenarioModule.disable();
        } finally {
            activeScenarios.remove(scenarioDefinition);
        }

        return true;
    }

    public boolean toggleScenario(ScenarioDefinition scenarioDefinition) {
        if (isScenarioActive(scenarioDefinition)) {
            disableScenario(scenarioDefinition);
            return false;
        }

        enableScenario(scenarioDefinition);
        return true;
    }

    public boolean isScenarioRegistered(ScenarioDefinition scenarioDefinition) {
        return registeredScenarios.containsKey(scenarioDefinition);
    }

    public boolean isScenarioActive(ScenarioDefinition scenarioDefinition) {
        return activeScenarios.containsKey(scenarioDefinition);
    }

    public ScenarioModule getRegisteredScenario(ScenarioDefinition scenarioDefinition) {
        ScenarioModule scenarioModule = registeredScenarios.get(scenarioDefinition);

        if (scenarioModule == null) {
            throw new IllegalArgumentException("Scenario is not registered: " + scenarioDefinition);
        }

        return scenarioModule;
    }

    public Optional<ScenarioModule> findActiveScenario(ScenarioDefinition scenarioDefinition) {
        return Optional.ofNullable(activeScenarios.get(scenarioDefinition));
    }

    public List<ScenarioModule> getRegisteredScenarios() {
        return List.copyOf(registeredScenarios.values());
    }

    public List<ScenarioModule> getActiveScenarios() {
        return List.copyOf(activeScenarios.values());
    }

    public List<ScenarioDefinition> getRegisteredScenarioDefinitions() {
        return List.copyOf(registeredScenarios.keySet());
    }

    public List<ScenarioDefinition> getActiveScenarioDefinitions() {
        return List.copyOf(activeScenarios.keySet());
    }
}