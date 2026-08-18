package org.heather.hardlands.module.scenario;

import org.heather.hardlands.common.item.inventory.InventoryItem;
import org.heather.hardlands.module.scenario.scenarios.AppleGroveScenario;
import org.heather.hardlands.module.scenario.scenarios.BonanzaScenario;
import org.heather.hardlands.module.scenario.scenarios.MagicManScenario;
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