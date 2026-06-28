package com.hardlands.scenario;

import com.hardlands.scenario.custom.*;

public final class Scenarios {
    private Scenarios() {}

    public static void initialize(ScenarioManager manager) {
        manager.registerScenario("cut_clean", new CutCleanScenario());
        manager.registerScenario("bonanza", new BonanzaScenario());
        manager.registerScenario("timber", new TimberScenario());
        manager.registerScenario("apple_grove", new AppleGroveScenario());
        manager.registerScenario("vein_miner", new VeinMinerScenario());
    }
}