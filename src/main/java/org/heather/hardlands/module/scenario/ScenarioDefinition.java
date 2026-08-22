package org.heather.hardlands.module.scenario;

import java.util.Locale;
import java.util.function.Supplier;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.heather.hardlands.common.item.InventoryItem;
import org.heather.hardlands.module.scenario.scenarios.AppleGroveScenario;
import org.heather.hardlands.module.scenario.scenarios.BonanzaScenario;
import org.heather.hardlands.module.scenario.scenarios.MagicManScenario;

public enum ScenarioDefinition {

    APPLE_GROVE("Apple Grove", AppleGroveScenario::new,
            Material.GOLDEN_APPLE, "Aumenta la obtención de manzanas y permite conseguir variantes doradas o encantadas."),

    BONANZA("Bonanza", BonanzaScenario::new,
            Material.GOLD_ORE, "Multiplica los recursos obtenidos al extraer minerales."),

    MAGIC_MAN("Magic Man", MagicManScenario::new,
            Material.ENCHANTING_TABLE, "Aplica los encantamientos configurados a sus herramientas respectivas.");

    private final String name;
    private final Supplier<Scenario> factory;
    private final ItemStack displayItem;

    ScenarioDefinition(String name, Supplier<Scenario> factory, Material material, String description) {
        this.name = name;
        this.factory = factory;
        this.displayItem = InventoryItem.createDisplayItem(material, "<yellow>" + name, description);
    }

    public String identifier() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public Scenario createScenario() {
        return this.factory.get();
    }

    public ItemStack createDisplayItem() {
        return this.displayItem.clone();
    }

    public String getName() {
        return this.name;
    }
}