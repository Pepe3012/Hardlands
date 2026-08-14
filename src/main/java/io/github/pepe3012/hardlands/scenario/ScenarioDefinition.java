package io.github.pepe3012.hardlands.scenario;

import io.github.pepe3012.hardlands.common.item.inventory.InventoryDisplay;
import io.github.pepe3012.hardlands.common.item.inventory.InventoryItem;
import io.github.pepe3012.hardlands.scenario.modules.AppleGroveScenario;
import io.github.pepe3012.hardlands.scenario.modules.BonanzaScenario;
import io.github.pepe3012.hardlands.scenario.modules.CutCleanScenario;
import io.github.pepe3012.hardlands.scenario.modules.HastyBoysScenario;
import io.github.pepe3012.hardlands.scenario.modules.TimberScenario;
import io.github.pepe3012.hardlands.scenario.modules.VeinMinerScenario;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ScenarioDefinition {

    CUT_CLEAN("cut_clean", "Cut Clean", CutCleanScenario::new,
            InventoryItem.display(Material.FURNACE, "Funde automáticamente los minerales y cocina los alimentos obtenidos.")),
    TIMBER("timber", "Timber", TimberScenario::new,
            InventoryItem.display(Material.IRON_AXE, "Tala árboles completos al romper uno de sus troncos con un hacha.")),
    APPLE_GROVE("apple_grove", "Apple Grove", AppleGroveScenario::new,
            InventoryItem.display(Material.GOLDEN_APPLE, "Aumenta la obtención de manzanas y permite convertirlas en doradas o encantadas al romper hojas.")),
    VEIN_MINER("vein_miner", "Vein Miner", VeinMinerScenario::new,
            InventoryItem.display(Material.COAL_ORE, "Extrae vetas completas de minerales al romper uno de sus bloques.")),
    HASTY_BOYS("hasty_boys", "Hasty Boys", HastyBoysScenario::new,
            InventoryItem.display(Material.DIAMOND_PICKAXE, "Aplica automáticamente Eficiencia e Irrompibilidad a las herramientas configuradas.")),
    BONANZA("bonanza", "Bonanza", BonanzaScenario::new,
            InventoryItem.display(Material.GOLD_ORE, "Multiplica la cantidad de recursos obtenidos al extraer minerales."));

    public static final List<String> IDENTIFIERS = Stream.of(values()).map(ScenarioDefinition::getIdentifier).toList();
    private static final Map<String, ScenarioDefinition> BY_IDENTIFIER = Stream.of(values()).collect(Collectors.toUnmodifiableMap(ScenarioDefinition::getIdentifier, definition -> definition));

    private final String identifier;
    private final String displayName;
    private final Supplier<ScenarioModule> moduleFactory;
    private final InventoryDisplay display;

    ScenarioDefinition(String identifier, String displayName, Supplier<ScenarioModule> moduleFactory, InventoryDisplay display) {
        this.identifier = identifier;
        this.displayName = displayName;
        this.moduleFactory = moduleFactory;
        this.display = display;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public ScenarioModule createModule() {
        return this.moduleFactory.get();
    }

    public ItemStack createDisplayItem() {
        return this.display.build("<yellow>" + this.displayName);
    }

    public static Optional<ScenarioDefinition> findByIdentifier(String identifier) {
        if (identifier == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(BY_IDENTIFIER.get(identifier.toLowerCase(Locale.ROOT)));
    }
}