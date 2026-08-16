package io.github.pepe3012.hardlands.module.scenario;

import io.github.pepe3012.hardlands.common.item.InventoryItem;
import io.github.pepe3012.hardlands.module.scenario.scenarios.AppleGroveScenario;
import io.github.pepe3012.hardlands.module.scenario.scenarios.BonanzaScenario;
import io.github.pepe3012.hardlands.module.scenario.scenarios.MagicManScenario;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.function.Supplier;

public enum ScenarioDefinition {

    APPLE_GROVE("Apple Grove", AppleGroveScenario::new, InventoryItem.display(Material.GOLDEN_APPLE, "Aumenta la obtención de manzanas y permite conseguir variantes doradas o encantadas.")),
    BONANZA("Bonanza", BonanzaScenario::new, InventoryItem.display(Material.GOLD_ORE, "Multiplica los recursos obtenidos al extraer minerales.")),
    MAGIC_MAN("Magic Man", MagicManScenario::new, InventoryItem.display(Material.ENCHANTING_TABLE, "Aplica los encantamientos configurados a sus herramientas respectivas."));

    private final String name;
    private final Supplier<Scenario> factory;
    private final InventoryItem.Display display;

    ScenarioDefinition(String name, Supplier<Scenario> factory, InventoryItem.Display display) {
        this.name = name;
        this.factory = factory;
        this.display = display;
    }

    public String getIdentifier() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public String getName() {
        return this.name;
    }

    public Scenario createScenario() {
        return this.factory.get();
    }

    public ItemStack createDisplayItem() {
        return this.display.build("<yellow>" + this.name);
    }
}