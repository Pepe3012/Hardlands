package com.hardlands.scenario;

import com.hardlands.scenario.scenarios.AppleGroveScenario;
import com.hardlands.scenario.scenarios.BonanzaScenario;
import com.hardlands.scenario.scenarios.CutCleanScenario;
import com.hardlands.scenario.scenarios.TimberScenario;
import com.hardlands.scenario.scenarios.VeinMinerScenario;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum ScenarioType {

    CUT_CLEAN("cut_clean", "Cut Clean", Material.FURNACE, "Funde minerales y cocina comida automáticamente.", CutCleanScenario::new),
    BONANZA("bonanza", "Bonanza", Material.GOLD_ORE, "Multiplica los drops obtenidos de los minerales.", BonanzaScenario::new),
    TIMBER("timber", "Timber", Material.IRON_AXE, "Tala árboles completos al romper uno de sus troncos.", TimberScenario::new),
    APPLE_GROVE("apple_grove", "Apple Grove", Material.APPLE, "Permite obtener manzanas al romper hojas.", AppleGroveScenario::new),
    VEIN_MINER("vein_miner", "Vein Miner", Material.DIAMOND_ORE, "Mina vetas completas de minerales de una sola vez.", VeinMinerScenario::new);

    public static final List<String> IDS = Arrays.stream(values()).map(ScenarioType::getId).toList();
    private static final Map<String, ScenarioType> BY_ID = Arrays.stream(values()).collect(Collectors.toUnmodifiableMap(ScenarioType::getId, type -> type));

    private final String id;
    private final String displayName;
    private final Material material;
    private final String description;
    private final Supplier<? extends Scenario> factory;

    public Scenario create() {
        return this.factory.get();
    }

    public static @Nullable ScenarioType fromId(String id) {
        return BY_ID.get(id.toLowerCase(Locale.ROOT));
    }
}