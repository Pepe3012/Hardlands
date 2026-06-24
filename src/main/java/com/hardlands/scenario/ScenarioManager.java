package com.hardlands.scenario;

import java.util.HashMap;
import java.util.Map;

public final class ScenarioManager {
    private static final Map<String, Scenario> REGISTERED_SCENARIOS = new HashMap<>();
    private static final Map<String, Scenario> ACTIVE_SCENARIOS = new HashMap<>();

    public void initializeScenarios() {
        ACTIVE_SCENARIOS.values().forEach(Scenario::initialize);
    }

    public void terminateScenarios() {
        ACTIVE_SCENARIOS.values().forEach(Scenario::terminate);
    }

    public Map<String, Scenario> getRegisteredScenarios() {
        return REGISTERED_SCENARIOS;
    }

    public Map<String, Scenario> getActiveScenarios() {
        return ACTIVE_SCENARIOS;
    }
}