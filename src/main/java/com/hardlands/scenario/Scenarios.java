package com.hardlands.scenario;

import com.hardlands.scenario.custom.BonanzaScenario;
import com.hardlands.scenario.custom.CutCleanScenario;

public final class Scenarios {
    public static final Scenario CUT_CLEAN = new CutCleanScenario();
    public static final Scenario BONANZA = new BonanzaScenario();

    private Scenarios() {}

    public static void initialize(ScenarioManager manager) {
        manager.registerScenario("cut_clean", CUT_CLEAN);
        manager.registerScenario("bonanza", BONANZA);
    }
}