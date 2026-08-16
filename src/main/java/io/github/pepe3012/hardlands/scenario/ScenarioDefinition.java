package io.github.pepe3012.hardlands.scenario;

import io.github.pepe3012.hardlands.common.item.inventory.InventoryDisplay;
import io.github.pepe3012.hardlands.common.item.InventoryItem;
import io.github.pepe3012.hardlands.scenario.modules.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.function.Supplier;

public enum ScenarioDefinition {

    APPLE_GROVE("Apple Grove", AppleGroveScenario::new,
            InventoryItem.display(Material.GOLDEN_APPLE, "Aumenta la obtención de manzanas y permite conseguir variantes doradas o encantadas.")),

    BONANZA("Bonanza", BonanzaScenario::new,
            InventoryItem.display(Material.GOLD_ORE, "Multiplica los recursos obtenidos al extraer minerales.")),

    MAGIC_MAN("Magic Man", MagicManScenario::new,
            InventoryItem.display(Material.ENCHANTING_TABLE, "Aplica los encantamientos configurados a sus herramientas respectivas.")),

    ;

    private final String name;
    private final Supplier<ScenarioModule> factory;
    private final InventoryItem.Display item;

    ScenarioDefinition(String name, Supplier<ScenarioModule> factory, InventoryItem.Display item) {
        this.name = name;
        this.factory = factory;
        this.item = item;
    }

    public ScenarioModule createModule() {
        return this.factory.get();
    }

    public ItemStack createDisplayItem() {
        return this.item.build("<yellow>" + this.name);
    }

    public String getName() {
        return this.name;
    }

    public String getIdentifier() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}