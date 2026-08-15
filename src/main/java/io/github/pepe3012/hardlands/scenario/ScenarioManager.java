package io.github.pepe3012.hardlands.scenario;

import org.bukkit.plugin.Plugin;

import java.util.*;

public final class ScenarioManager {

    private final Map<ScenarioDefinition, ScenarioModule> registeredScenarios = new EnumMap<>(ScenarioDefinition.class);
    private final Set<ScenarioDefinition> activeScenarios = EnumSet.noneOf(ScenarioDefinition.class);
    private final Plugin plugin;

    public ScenarioManager(final Plugin plugin) {
        this.plugin = plugin;
    }

    public void registerScenarios(ScenarioDefinition... definitions) {
        for (ScenarioDefinition definition : definitions) {
            if (this.registeredScenarios.putIfAbsent(definition, definition.createModule()) != null) {
                throw new IllegalArgumentException("Scenario is already registered: " + definition);
            }
        }
    }

    public boolean enableScenario(ScenarioDefinition definition) {
        if (this.activeScenarios.contains(definition)) {
            return false;
        }

        ScenarioModule module = this.getRegisteredScenario(definition);
        module.enable(this.plugin);
        this.activeScenarios.add(definition);

        return true;
    }

    public boolean disableScenario(ScenarioDefinition definition) {
        if (!this.activeScenarios.contains(definition)) {
            return false;
        }

        try {
            this.getRegisteredScenario(definition).disable();
        } finally {
            this.activeScenarios.remove(definition);
        }

        return true;
    }

    public boolean toggleScenario(ScenarioDefinition definition) {
        return this.isScenarioActive(definition)
                ? this.disableScenario(definition)
                : this.enableScenario(definition);
    }

    public boolean isScenarioRegistered(ScenarioDefinition definition) {
        return this.registeredScenarios.containsKey(definition);
    }

    public boolean isScenarioActive(ScenarioDefinition definition) {
        return this.activeScenarios.contains(definition);
    }

    public ScenarioModule getRegisteredScenario(ScenarioDefinition definition) {
        ScenarioModule module = this.registeredScenarios.get(definition);

        if (module == null) {
            throw new IllegalArgumentException("Scenario is not registered: " + definition);
        }

        return module;
    }

    public Optional<ScenarioModule> findActiveScenario(ScenarioDefinition definition) {
        return this.isScenarioActive(definition)
                ? Optional.of(this.getRegisteredScenario(definition))
                : Optional.empty();
    }

    public List<ScenarioModule> getRegisteredScenarios() {
        return List.copyOf(this.registeredScenarios.values());
    }

    public List<ScenarioModule> getActiveScenarios() {
        return this.activeScenarios.stream()
                .map(this::getRegisteredScenario)
                .toList();
    }

    public List<ScenarioDefinition> getRegisteredScenarioDefinitions() {
        return List.copyOf(this.registeredScenarios.keySet());
    }

    public List<ScenarioDefinition> getActiveScenarioDefinitions() {
        return List.copyOf(this.activeScenarios);
    }
}