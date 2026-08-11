package org.heather.hardlands.scenario;

import org.heather.hardlands.common.item.InventoryItem;
import org.heather.hardlands.common.item.InventoryItem.InventoryDisplay;
import org.heather.hardlands.scenario.modules.AppleGroveScenario;
import org.heather.hardlands.scenario.modules.BonanzaScenario;
import org.heather.hardlands.scenario.modules.CutCleanScenario;
import org.heather.hardlands.scenario.modules.HastyBoysScenario;
import org.heather.hardlands.scenario.modules.TimberScenario;
import org.heather.hardlands.scenario.modules.VeinMinerScenario;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum ScenarioDefinition {

    CUT_CLEAN("cut_clean", "Cut Clean", CutCleanScenario::new, InventoryItem.createInventoryDisplay(Material.FURNACE, "Funde automáticamente los minerales y cocina los alimentos obtenidos.")),
    BONANZA("bonanza", "Bonanza", BonanzaScenario::new, InventoryItem.createInventoryDisplay(Material.GOLD_ORE, "Multiplica la cantidad de recursos obtenidos al extraer minerales.")),
    TIMBER("timber", "Timber", TimberScenario::new, InventoryItem.createInventoryDisplay(Material.IRON_AXE, "Tala árboles completos al romper uno de sus troncos con un hacha.")),
    APPLE_GROVE("apple_grove", "Apple Grove", AppleGroveScenario::new, InventoryItem.createInventoryDisplay(Material.APPLE, "Aumenta la obtención de manzanas y permite convertirlas en doradas o encantadas al romper hojas.")),
    VEIN_MINER("vein_miner", "Vein Miner", VeinMinerScenario::new, InventoryItem.createInventoryDisplay(Material.DIAMOND_ORE, "Extrae vetas completas de minerales al romper uno de sus bloques.")),
    HASTY_BOYS("hasty_boys", "Hasty Boys", HastyBoysScenario::new, InventoryItem.createInventoryDisplay(Material.DIAMOND_PICKAXE, "Aplica automáticamente Efficiency y Unbreaking a las herramientas configuradas."));

    public static final List<String> IDENTIFIERS = Arrays.stream(values()).map(ScenarioDefinition::getIdentifier).toList();
    private static final Map<String, ScenarioDefinition> BY_IDENTIFIER = Arrays.stream(values()).collect(Collectors.toUnmodifiableMap(ScenarioDefinition::getIdentifier, Function.identity()));

    private final String identifier;
    private final String displayName;
    private final Supplier<ScenarioModule> moduleFactory;
    private final InventoryDisplay display;

    public ScenarioModule createModule() {
        return this.moduleFactory.get();
    }

    public static Optional<ScenarioDefinition> findByIdentifier(String identifier) {
        if (identifier == null) return Optional.empty();
        return Optional.ofNullable(BY_IDENTIFIER.get(identifier.toLowerCase(Locale.ROOT)));
    }
}