package com.hardlands.scenario;

import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ScenarioManager {

    private final Map<ScenarioType, Scenario> scenarios = new EnumMap<>(ScenarioType.class);
    private final Set<ScenarioType> activeTypes = EnumSet.noneOf(ScenarioType.class);
    private final List<ScenarioType> registeredTypes = List.of(ScenarioType.values());

    public ScenarioManager() {
        for (ScenarioType type : this.registeredTypes) {
            this.scenarios.put(type, type.create());
        }
    }

    public boolean enable(ScenarioType type) {
        if (!this.activeTypes.add(type)) return false;

        this.scenarios.get(type).enable();
        return true;
    }

    public boolean disable(ScenarioType type) {
        if (!this.activeTypes.remove(type)) return false;

        this.scenarios.get(type).disable();
        return true;
    }

    public boolean toggle(ScenarioType type) {
        if (this.isActive(type)) {
            this.disable(type);
            return false;
        }

        this.enable(type);
        return true;
    }

    public boolean isActive(ScenarioType type) {
        return this.activeTypes.contains(type);
    }

    public Scenario get(ScenarioType type) {
        return this.scenarios.get(type);
    }

    public @Nullable Scenario getActive(ScenarioType type) {
        return this.isActive(type) ? this.scenarios.get(type) : null;
    }

    public List<ScenarioType> getRegisteredScenarioTypes() {
        return this.registeredTypes;
    }

    public List<Scenario> getActive() {
        return this.activeTypes.stream().map(this.scenarios::get).toList();
    }

    public Set<ScenarioType> getActiveScenarioTypes() {
        return Set.copyOf(this.activeTypes);
    }
}