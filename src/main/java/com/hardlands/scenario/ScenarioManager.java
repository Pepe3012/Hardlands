package com.hardlands.scenario;

import org.jetbrains.annotations.Unmodifiable;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ScenarioManager {

    private final Map<ScenarioType, Scenario> active = new EnumMap<>(ScenarioType.class);

    public boolean enable(ScenarioType type) {
        if (this.active.containsKey(type)) return false;
        Scenario scenario = type.create();
        this.active.put(type, scenario);
        scenario.enable();
        return true;
    }

    public boolean disable(ScenarioType type) {
        Scenario scenario = this.active.remove(type);
        if (scenario == null) return false;
        scenario.disable();
        return true;
    }

    public boolean isActive(ScenarioType type) {
        return this.active.containsKey(type);
    }

    public Scenario getActive(ScenarioType type) {
        return this.active.get(type);
    }

    @Unmodifiable
    public List<Scenario> getActive() {
        return List.copyOf(this.active.values());
    }

    @Unmodifiable
    public Set<ScenarioType> getActiveScenarioTypes() {
        return Set.copyOf(this.active.keySet());
    }
}