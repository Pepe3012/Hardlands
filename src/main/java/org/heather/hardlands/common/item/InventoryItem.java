package org.heather.hardlands.common.item;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.heather.hardlands.Hardlands;
import org.heather.hardlands.common.inventory.InventoryDefinition;
import org.heather.hardlands.core.config.Option;
import org.heather.hardlands.module.world.PregenerationManager;
import org.heather.hardlands.module.world.WorldManager;
import org.heather.hardlands.util.text.TextFormatter;

public enum InventoryItem {

    PREVIOUS("MHF_ArrowLeft",
            "Anterior",
            "Regresa al menú o página anterior.",
            open(() -> InventoryDefinition.MAIN)),

    NEXT("MHF_ArrowRight",
            "Siguiente",
            "Avanza a la siguiente página."),

    PREPARATION(InventoryItem::createPreparationItem, Map.of(ClickType.LEFT, InventoryItem::togglePregeneration)),

    SCENARIOS(Material.CHERRY_SAPLING,
            "Escenarios",
            "Activa, desactiva y configura los escenarios de la partida.",
            open(() -> InventoryDefinition.SCENARIOS)),

    PLAYERS(Material.PLAYER_HEAD,
            "Jugadores",
            "Administra los jugadores de la partida.",
            open(() -> InventoryDefinition.PLAYERS)),

    GENERAL(Material.COMPARATOR,
            "General",
            "Configura las opciones generales de la partida.",
            open(() -> InventoryDefinition.GENERAL)),

    PHASES(Material.CLOCK,
            "Fases",
            "Configura la progresión y los tiempos de las fases de la partida.",
            open(() -> InventoryDefinition.PHASES)),

    WORLD("KEYKOTV",
            "Mundo",
            "Configura la generación y los límites del mundo.",
            open(() -> InventoryDefinition.WORLD)),

    PRESETS(Material.WRITABLE_BOOK,
            "Plantillas",
            "Administra las plantillas de configuración.",
            open(() -> InventoryDefinition.PRESETS)),

    ;

    private static final String DESCRIPTION_PREFIX = "<gray>";
    private final Supplier<ItemStack> itemSupplier;
    private final Map<ClickType, ClickHandler> clickHandlers;

    InventoryItem(Material material, String name, String description) {
        this(material, name, description, Map.of());
    }

    InventoryItem(Material material, String name, String description, ClickHandler handler) {
        this(material, name, description, Map.of(ClickType.LEFT, handler));
    }

    InventoryItem(Material material, String name, String description, Map<ClickType, ClickHandler> clickHandlers) {
        this(() -> createDisplayItem(material, name, description), clickHandlers);
    }

    InventoryItem(String skullOwner, String name, String description) {
        this(skullOwner, name, description, Map.of());
    }

    InventoryItem(String skullOwner, String name, String description, ClickHandler handler) {
        this(skullOwner, name, description, Map.of(ClickType.LEFT, handler));
    }

    InventoryItem(String skullOwner, String name, String description, Map<ClickType, ClickHandler> clickHandlers) {
        this(() -> new ItemBuilder(Material.PLAYER_HEAD)
                .skullOwner(skullOwner)
                .name(name)
                .lore(DESCRIPTION_PREFIX + description)
                .build(), clickHandlers);
    }

    InventoryItem(Supplier<ItemStack> itemSupplier, Map<ClickType, ClickHandler> clickHandlers) {
        this.itemSupplier = itemSupplier;
        this.clickHandlers = clickHandlers;
    }

    public ItemStack buildItem() {
        return new ItemBuilder(this.itemSupplier.get())
                .setId(this.name())
                .build();
    }

    public boolean handleClick(InventoryClickEvent event) {
        ClickHandler handler = this.clickHandlers.get(event.getClick());

        return handler != null && handler.handle(event);
    }

    public static Optional<InventoryItem> findByItem(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return Optional.empty();
        }

        return new ItemBuilder(item)
                .findId()
                .flatMap(InventoryItem::findById);
    }

    public static ItemStack createDisplayItem(Material material, String name, String description) {
        return new ItemBuilder(material)
                .name(name)
                .lore(DESCRIPTION_PREFIX + description)
                .build();
    }

    private static ClickHandler open(Supplier<InventoryDefinition> definition) {
        return event -> {
            if (!(event.getWhoClicked() instanceof Player player)) {
                return false;
            }

            definition.get().openInventory(player);
            return true;
        };
    }

    private static ItemStack createPreparationItem() {
        WorldManager worldManager = Hardlands.getInstance().getWorldManagerOrThrow();
        PregenerationManager pregenerationManager = worldManager.getPregenerationManager();

        Option<Integer> survivalSizeOption = worldManager.getSurvivalSizeOption();
        Integer borderSize = survivalSizeOption.getValue();

        PregenerationManager.State state = pregenerationManager.getState();
        float progress = pregenerationManager.getProgress();

        String borderSizeText = survivalSizeOption.isValid() && borderSize != null
                ? "{%1$d × %1$d}".formatted(borderSize)
                : "<gray>Inválido";

        String progressText = progress >= 100.0F
                ? "{%.1f%%}".formatted(progress)
                : "<gray>%.1f%%".formatted(progress);

        return new ItemBuilder(state.getMaterial())
                .name(TextFormatter.formatTinyCaps("Preparación"))
                .formattedLore(
                        "Establece los {World Borders} según lo configurado e inicia la {pregeneración} de los mundos abiertos.",
                        "",
                        "World Border: %s".formatted(borderSizeText),
                        "Progreso: %s".formatted(progressText))
                .addLore(Component.text("Estado: ", NamedTextColor.WHITE).append(state.display()))
                .build();
    }

    private static boolean togglePregeneration(InventoryClickEvent event) {
        WorldManager worldManager = Hardlands.getInstance().getWorldManagerOrThrow();
        PregenerationManager pregenerationManager = worldManager.getPregenerationManager();

        switch (pregenerationManager.getState()) {
            case IDLE -> {
                if (!worldManager.isConfigurationValid()) {
                    return false;
                }

                worldManager.pregenerate();
            }

            case RUNNING -> pregenerationManager.pause();
            case PAUSED -> pregenerationManager.resume();

            case COMPLETED -> {
                return false;
            }
        }

        event.setCurrentItem(PREPARATION.buildItem());
        return true;
    }

    private static Optional<InventoryItem> findById(String id) {
        try {
            return Optional.of(valueOf(id));
        } catch (IllegalArgumentException _) {
            return Optional.empty();
        }
    }

    @FunctionalInterface
    public interface ClickHandler {

        boolean handle(InventoryClickEvent event);
    }
}