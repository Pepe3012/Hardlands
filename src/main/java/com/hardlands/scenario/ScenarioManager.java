package com.hardlands.scenario;

import com.hardlands.Hardlands;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class ScenarioManager {
    private final Map<String, Scenario> registeredScenarios = new HashMap<>();
    private final Map<String, Scenario> activeScenarios = new HashMap<>();
    private final Hardlands plugin;

    public ScenarioManager(Hardlands plugin) {
        this.plugin = plugin;
    }

    public boolean enableScenario(String id) {
        Scenario scenario = this.registeredScenarios.get(id);
        if (scenario == null || this.activeScenarios.containsKey(id)) return false;
        this.activeScenarios.put(id, scenario);
        scenario.enable();
        return true;
    }

    public boolean disableScenario(String id) {
        Scenario scenario = this.activeScenarios.remove(id);
        if (scenario == null) return false;
        scenario.disable();
        return true;
    }

    public void registerScenario(String id, Scenario scenario) {
        scenario.setPlugin(this.plugin);
        this.registeredScenarios.put(id, scenario);
    }

    public @Nullable String getScenarioId(Scenario scenario) {
        for (Map.Entry<String, Scenario> entry : this.registeredScenarios.entrySet()) {
            if (entry.getValue().equals(scenario)) return entry.getKey();
        }
        return null;
    }

    public Map<String, Scenario> getRegisteredScenarios() {
        return registeredScenarios;
    }

    public Map<String, Scenario> getActiveScenarios() {
        return activeScenarios;
    }
}