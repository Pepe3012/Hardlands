package org.heather.hardlands.common.item;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.heather.hardlands.common.inventory.InventoryDefinition;
import org.heather.hardlands.core.config.Option;
import org.heather.hardlands.module.world.PregenerationManager;
import org.heather.hardlands.module.world.WorldManager;
import org.heather.hardlands.util.text.TextFormatter;

public enum InventoryItem {

    PREVIOUS(createHeadItem("MHF_ArrowLeft", "Anterior", "Regresa al menú o página anterior."),
            createClickDefinition(ClickType.LEFT, (inventory, player) ->
                    InventoryDefinition.findDefinition(inventory)
                            .ifPresent(definition -> definition.openParent(player)))),

    NEXT(createHeadItem("MHF_ArrowRight", "Siguiente", "Avanza a la siguiente página."),
            createClickDefinition(ClickType.LEFT, (inventory, player) -> {

            })),

    // Menu Definitions
    SCENARIOS(createMenuDefinition(
            () -> InventoryDefinition.SCENARIOS,
            Material.CHERRY_SAPLING,
            "Activa, desactiva y configura los escenarios de la partida.")),

    PLAYERS(createMenuDefinition(
            () -> InventoryDefinition.PLAYERS,
            Material.PLAYER_HEAD,
            "Administra los jugadores de la partida.")),

    DURATION(createMenuDefinition(
            () -> InventoryDefinition.DURATION,
            Material.COMPARATOR,
            "Configura las opciones generales de la partida.")),

    VANILLA_CHANGES(createMenuDefinition(
            () -> InventoryDefinition.VANILLA_CHANGES,
            Material.GRASS_BLOCK,
            "Consulta y configura los cambios realizados al juego base.")),

    WORLD(createMenuDefinition(
            () -> InventoryDefinition.WORLD,
            () -> new ItemBuilder(Material.PLAYER_HEAD).skullOwner("KEYKOTV"),
            "Configura la generación y los límites del mundo.")),

    PRESETS(createMenuDefinition(
            () -> InventoryDefinition.PRESETS,
            Material.WRITABLE_BOOK,
            "Administra las plantillas de configuración."));

    private static final String DESCRIPTION_PREFIX = "<gray>";

    private final Supplier<ItemStack> itemFactory;
    private final List<ClickDefinition> clickDefinitions;

    InventoryItem(Supplier<ItemStack> itemFactory, ClickDefinition... clickDefinitions) {
        this.itemFactory = itemFactory;
        this.clickDefinitions = List.of(clickDefinitions);
    }

    InventoryItem(MenuDefinition menuDefinition) {
        this(menuDefinition.itemFactory(), menuDefinition.clickDefinition());
    }

    public ItemStack buildItem() {
        return new ItemBuilder(this.itemFactory.get())
                .setId(this.name())
                .build();
    }

    public boolean handleClick(Inventory inventory, Player player, ClickType type) {
        for (ClickDefinition clickDefinition : this.clickDefinitions) {
            if (clickDefinition.type() == type) {
                clickDefinition.action().handle(inventory, player);
                return true;
            }
        }

        return false;
    }

    public static Optional<InventoryItem> findByItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return Optional.empty();

        return new ItemBuilder(item)
                .findId()
                .flatMap(InventoryItem::findByIdentifier);
    }

    public static ItemStack createPreparationItem(WorldManager worldManager) {
        PregenerationManager pregenerationManager = worldManager.getPregenerationManager();
        PregenerationManager.State state = pregenerationManager.getState();

        Option<Integer> survivalSizeOption = worldManager.getSurvivalSizeOption();
        Integer borderSize = survivalSizeOption.getValue();

        String borderSizeText = survivalSizeOption.isValid() && borderSize != null
                ? "{%1$d × %1$d}".formatted(borderSize)
                : "<gray>Inválido";

        float progress = pregenerationManager.getProgress();
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

    public static DisplayItem createDisplayItem(Material material, String description) {
        return new DisplayItem(material, description);
    }

    private static Optional<InventoryItem> findByIdentifier(String identifier) {
        try {
            return Optional.of(valueOf(identifier));
        } catch (IllegalArgumentException _) {
            return Optional.empty();
        }
    }

    private static MenuDefinition createMenuDefinition(
            Supplier<InventoryDefinition> definitionFactory,
            Material material,
            String description) {
        return createMenuDefinition(
                definitionFactory,
                () -> new ItemBuilder(material),
                description);
    }

    private static MenuDefinition createMenuDefinition(
            Supplier<InventoryDefinition> definitionFactory,
            Supplier<ItemBuilder> builderFactory,
            String description) {
        return new MenuDefinition(
                () -> builderFactory.get()
                        .name(definitionFactory.get().getTitle())
                        .lore(DESCRIPTION_PREFIX + description)
                        .build(),
                createClickDefinition(ClickType.LEFT, (_, player) ->
                        definitionFactory.get().openInventory(player)));
    }

    private static Supplier<ItemStack> createHeadItem(String owner, String name, String description) {
        return () -> new ItemBuilder(Material.PLAYER_HEAD)
                .skullOwner(owner)
                .name(name)
                .lore(DESCRIPTION_PREFIX + description)
                .build();
    }

    private static ClickDefinition createClickDefinition(ClickType type, ClickAction action) {
        return new ClickDefinition(type, action);
    }

    @FunctionalInterface
    private interface ClickAction {
        void handle(Inventory inventory, Player player);
    }

    private record ClickDefinition(ClickType type, ClickAction action) {}

    private record MenuDefinition(Supplier<ItemStack> itemFactory, ClickDefinition clickDefinition) {}

    public record DisplayItem(Material material, String description) {

        public ItemStack buildItem(String name) {
            return new ItemBuilder(this.material)
                    .name(name)
                    .lore(DESCRIPTION_PREFIX + this.description)
                    .build();
        }
    }
}