package com.hardlands.scenario;

import com.hardlands.scenario.scenarios.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum ScenarioType {

    CUT_CLEAN("cut_clean", CutCleanScenario::new),
    BONANZA("bonanza", BonanzaScenario::new),
    TIMBER("timber", TimberScenario::new),
    APPLE_GROVE("apple_grove", AppleGroveScenario::new),
    VEIN_MINER("vein_miner", VeinMinerScenario::new);

    private static final Map<String, ScenarioType> SCENARIO_TYPES_BY_ID = Arrays.stream(values()).collect(Collectors.toUnmodifiableMap(ScenarioType::getId, type -> type));
    public static final List<String> IDS = List.copyOf(SCENARIO_TYPES_BY_ID.keySet());

    private final String id;
    private final Supplier<? extends Scenario> factory;

    public Scenario create() {
        return this.factory.get();
    }

    public static ScenarioType fromId(String id) {
        return SCENARIO_TYPES_BY_ID.get(id.toLowerCase(Locale.ROOT));
    }
}