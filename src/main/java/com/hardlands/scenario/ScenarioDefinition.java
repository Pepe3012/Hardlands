package com.hardlands.scenario;

import com.hardlands.scenario.modules.AppleGroveScenario;
import com.hardlands.scenario.modules.BonanzaScenario;
import com.hardlands.scenario.modules.CutCleanScenario;
import com.hardlands.scenario.modules.TimberScenario;
import com.hardlands.scenario.modules.VeinMinerScenario;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum ScenarioDefinition {

    CUT_CLEAN("cut_clean", "Cut Clean", Material.FURNACE, "Funde minerales y cocina comida automáticamente.", CutCleanScenario::new),
    BONANZA("bonanza", "Bonanza", Material.GOLD_ORE, "Multiplica los drops obtenidos de los minerales.", BonanzaScenario::new),
    TIMBER("timber", "Timber", Material.IRON_AXE, "Tala árboles completos al romper uno de sus troncos.", TimberScenario::new),
    APPLE_GROVE("apple_grove", "Apple Grove", Material.APPLE, "Permite obtener manzanas al romper hojas.", AppleGroveScenario::new),
    VEIN_MINER("vein_miner", "Vein Miner", Material.DIAMOND_ORE, "Mina vetas completas de minerales de una sola vez.", VeinMinerScenario::new);

    public static final List<String> IDENTIFIERS = Arrays.stream(values()).map(ScenarioDefinition::getIdentifier).toList();
    private static final Map<String, ScenarioDefinition> DEFINITIONS_BY_IDENTIFIER = Arrays.stream(values()).collect(Collectors.toUnmodifiableMap(ScenarioDefinition::getIdentifier, definition -> definition));

    private final String identifier;
    private final String displayName;
    private final Material iconMaterial;
    private final String description;
    private final Supplier<ScenarioModule> moduleFactory;

    public ScenarioModule createModule() {
        return moduleFactory.get();
    }

    public static Optional<ScenarioDefinition> findByIdentifier(String identifier) {
        if (identifier == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(DEFINITIONS_BY_IDENTIFIER.get(identifier.toLowerCase(Locale.ROOT)));
    }
}