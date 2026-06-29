package com.hardlands.scenario;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ScenarioManager {
    private final Map<ScenarioTypes, Scenario> activeScenarios = new EnumMap<>(ScenarioTypes.class);

    public boolean enableScenario(@NotNull ScenarioTypes type) {
        if (this.activeScenarios.containsKey(type)) return false;
        Scenario scenario = type.createScenario();
        this.activeScenarios.put(type, scenario);
        scenario.enable();
        return true;
    }

    public boolean disableScenario(@NotNull ScenarioTypes type) {
        Scenario scenario = this.activeScenarios.remove(type);
        if (scenario == null) return false;
        scenario.disable();
        return true;
    }

    public boolean isActive(@NotNull ScenarioTypes type) {
        return this.activeScenarios.containsKey(type);
    }

    public @Nullable Scenario getActiveScenario(@NotNull ScenarioTypes type) {
        return this.activeScenarios.get(type);
    }

    public List<Scenario> getActiveScenarios() {
        return List.copyOf(this.activeScenarios.values());
    }

    public Set<ScenarioTypes> getActiveScenarioTypes() {
        return Set.copyOf(this.activeScenarios.keySet());
    }
}