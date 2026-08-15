package io.github.pepe3012.hardlands.scenario;

import io.github.pepe3012.hardlands.common.item.inventory.InventoryDisplay;
import io.github.pepe3012.hardlands.common.item.inventory.InventoryItem;
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

    // BERSERK("berserk", "Berserk", BerserkScenario::new, InventoryItem.display(Material.IRON_SWORD, "Otorga Regeneración IV, Fuerza I y Velocidad II durante 20 segundos al eliminar a un jugador.")),
    // FINAL_HEAL("final_heal", "Final Heal", FinalHealScenario::new, InventoryItem.display(Material.GLISTERING_MELON_SLICE, "Restaura completamente la salud de todos los jugadores en el momento configurado.")),
    // FIREPROOF("fireproof", "Fireproof", FireproofScenario::new, InventoryItem.display(Material.MAGMA_CREAM, "Protege los objetos soltados del fuego y la lava.")),
    // HEART_HUNTER("heart_hunter", "Heart Hunter", HeartHunterScenario::new, InventoryItem.display(Material.REDSTONE, "Aumenta la vida máxima al descubrir mobs, completar logros y eliminar jugadores.")),
    // LIMITLESS("limitless", "Limitless", LimitlessScenario::new, InventoryItem.display(Material.ENCHANTED_BOOK, "Elimina el límite de encantamientos.")),
    // MAGIC_MAN("magic_man", "Magic Man", MagicManScenario::new, InventoryItem.display(Material.ENCHANTING_TABLE, "Aplica los encantamientos configurados a sus herramientas respectivas sin reemplazar niveles superiores.")),
    // PERFECT_GAME("perfect_game", "Perfect Game", PerfectGameScenario::new, InventoryItem.display(Material.CLOCK, "Recompensa periódicamente a los jugadores que no reciben daño.")),
    // PLAYER_RADAR("player_radar", "Player Radar", PlayerRadarScenario::new, InventoryItem.display(Material.COMPASS, "Activa la barra de ubicación.")),
    // STARTER_ITEMS("starter_items", "Starter Items", StarterItemsScenario::new, InventoryItem.display(Material.BUNDLE, "Otorga los objetos configurados al unirse al mundo por primera vez.")),
    // TOMB_BOMB("tomb_bomb", "Tomb Bomb", TombBombScenario::new, InventoryItem.display(Material.TNT, "Guarda el inventario de los jugadores eliminados en un cofre que explota después de unos segundos."));

    ;

    private final String displayName;
    private final Supplier<ScenarioModule> moduleFactory;
    private final InventoryDisplay display;

    ScenarioDefinition(String displayName, Supplier<ScenarioModule> moduleFactory, InventoryDisplay display) {
        this.displayName = displayName;
        this.moduleFactory = moduleFactory;
        this.display = display;
    }

    public ScenarioModule createModule() {
        return this.moduleFactory.get();
    }

    public ItemStack createDisplayItem() {
        return this.display.build("<yellow>" + this.displayName);
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getIdentifier() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}