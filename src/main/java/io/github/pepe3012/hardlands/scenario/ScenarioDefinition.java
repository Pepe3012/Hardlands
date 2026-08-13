package io.github.pepe3012.hardlands.scenario;

import io.github.pepe3012.hardlands.common.item.inventory.InventoryDisplay;
import io.github.pepe3012.hardlands.common.item.inventory.InventoryItem;
import io.github.pepe3012.hardlands.scenario.modules.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public enum ScenarioDefinition {

    CUT_CLEAN("cut_clean", "Cut Clean", CutCleanScenario::new, InventoryItem.display(Material.FURNACE, "Funde automáticamente los minerales y cocina los alimentos obtenidos.")),
    TIMBER("timber", "Timber", TimberScenario::new, InventoryItem.display(Material.IRON_AXE, "Tala árboles completos al romper uno de sus troncos con un hacha.")),
    APPLE_GROVE("apple_grove", "Apple Grove", AppleGroveScenario::new, InventoryItem.display(Material.GOLDEN_APPLE, "Aumenta la obtención de manzanas y permite convertirlas en doradas o encantadas al romper hojas.")),
    VEIN_MINER("vein_miner", "Vein Miner", VeinMinerScenario::new, InventoryItem.display(Material.COAL_ORE, "Extrae vetas completas de minerales al romper uno de sus bloques.")),
    HASTY_BOYS("hasty_boys", "Hasty Boys", HastyBoysScenario::new, InventoryItem.display(Material.DIAMOND_PICKAXE, "Aplica automáticamente Eficiencia e Irrompibilidad a las herramientas configuradas.")),
    BONANZA("bonanza", "Bonanza", BonanzaScenario::new, InventoryItem.display(Material.GOLD_ORE, "Multiplica la cantidad de recursos obtenidos al extraer minerales.")),

    // PERFECT_GAME("perfect_game", "Perfect Game", PerfectGameScenario::new, InventoryItem.display(Material.HEART_OF_THE_SEA, "Recompensa periódicamente a los jugadores que permanecen sin recibir daño PvE.")),
    // SPOILS("spoils", "Spoils", SpoilsScenario::new, InventoryItem.display(Material.GOLDEN_APPLE, "Otorga beneficios configurados al jugador después de conseguir una eliminación.")),
    // TOMB_BOMB("tomb_bomb", "TombBomb", TombBombScenario::new, InventoryItem.display(Material.TNT, "Coloca el inventario de los jugadores eliminados en un cofre que explota después de unos segundos.")),
    // DEAD_EYE("dead_eye", "Dead Eye", DeadEyeScenario::new, InventoryItem.display(Material.ARROW, "Recompensa los impactos con arco realizados desde largas distancias.")),
    // ANTI_BURN("anti_burn", "Anti Burn", AntiBurnScenario::new, InventoryItem.display(Material.FIRE_CHARGE, "Evita que los objetos soltados sean destruidos por el fuego o la lava.")),
    // FINAL_HEAL("final_heal", "Final Heal", FinalHealScenario::new, InventoryItem.display(Material.GOLDEN_APPLE, "Restaura completamente la salud de todos los jugadores en el momento configurado.")),

    // SCARCITY("scarcity", "Scarcity", ScarcityScenario::new, InventoryItem.display(Material.DIAMOND, "Impide obtener mediante minería los recursos configurados.")),
    // DAMAGE_IMPACT("damage_impact", "Damage Impact", DamageImpactScenario::new, InventoryItem.display(Material.TOTEM_OF_UNDYING, "Modifica la intensidad de los tipos de daño configurados, permitiendo cancelarlos, reducirlos o amplificarlos."));
    // BINDINGS("bindings", "Bindings", BindingsScenario::new, InventoryItem.display(Material.BARRIER, "Restringe el uso de los objetos y mecánicas configurados durante la partida."));

    // PARANOIA("paranoia", "Paranoia", ParanoiaScenario::new, InventoryItem.display(Material.COMPASS, "Revela las coordenadas de los jugadores al realizar acciones configuradas.")),
    // BLOOD_TOLL("blood_toll", "Blood Toll", BloodTollScenario::new, InventoryItem.display(Material.REDSTONE, "Inflige daño al jugador al realizar determinadas acciones configuradas.")),

    // * Fun and games
    // DROPS_RANDOM("drops_random", "Drops Random", DropsRandomScenario::new, InventoryItem.display(Material.DROPPER, "Reemplaza cada objeto soltado por bloques y entidades por un objeto aleatorio.")),
    // FLOWER_POWER("flower_power", "Flower Power", FlowerPowerScenario::new, InventoryItem.display(Material.POPPY, "Hace que las flores suelten objetos aleatorios al romperlas.")),
    // LUCKY_LEAVES("lucky_leaves", "Lucky Leaves", LuckyLeavesScenario::new, InventoryItem.display(Material.OAK_LEAVES, "Permite obtener recompensas especiales al romper hojas o cuando estas se descomponen.")),
    // GAPPLE_ROULETTE("gapple_roulette", "Gapple Roulette", GappleRouletteScenario::new, InventoryItem.display(Material.GOLDEN_APPLE, "Otorga un efecto aleatorio al consumir una manzana dorada.")),
    // MOLES("moles", "Moles", MolesScenario::new, InventoryItem.display(Material.SPYGLASS, "Selecciona secretamente a un traidor dentro de cada equipo con objetivos propios."));

    ;

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

    public static final List<String> IDENTIFIERS = Arrays.stream(values()).map(ScenarioDefinition::getIdentifier).toList();
    private static final Map<String, ScenarioDefinition> BY_IDENTIFIER = Arrays.stream(values()).collect(Collectors.toUnmodifiableMap(ScenarioDefinition::getIdentifier, Function.identity()));

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