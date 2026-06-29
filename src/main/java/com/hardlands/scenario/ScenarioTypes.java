package com.hardlands.scenario;

import com.hardlands.scenario.custom.AppleGroveScenario;
import com.hardlands.scenario.custom.BonanzaScenario;
import com.hardlands.scenario.custom.CutCleanScenario;
import com.hardlands.scenario.custom.TimberScenario;
import com.hardlands.scenario.custom.VeinMinerScenario;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public enum ScenarioTypes {
    CUT_CLEAN("cut_clean", CutCleanScenario::new),
    BONANZA("bonanza", BonanzaScenario::new),
    TIMBER("timber", TimberScenario::new),
    APPLE_GROVE("apple_grove", AppleGroveScenario::new),
    VEIN_MINER("vein_miner", VeinMinerScenario::new);

    private final String id;
    private final Supplier<Scenario> factory;

    ScenarioTypes(@NotNull String id, @NotNull Supplier<Scenario> factory) {
        this.id = id;
        this.factory = factory;
    }

    public @NotNull String getId() {
        return this.id;
    }

    public @NotNull Scenario createScenario() {
        return this.factory.get();
    }

    public static List<String> getIds() {
        return Arrays.stream(values()).map(ScenarioTypes::getId).toList();
    }

    public static @Nullable ScenarioTypes byId(@NotNull String id) {
        for (ScenarioTypes type : values()) {
            if (type.id.equalsIgnoreCase(id)) {
                return type;
            }
        }
        return null;
    }
}