package io.github.pepe3012.hardlands.common.item;

import io.github.pepe3012.hardlands.module.inventory.InventoryDefinition;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public enum InventoryItem {

    PREVIOUS(
            head("MHF_ArrowLeft", "Anterior", "Regresa al menú o página anterior."),
            click(ClickType.LEFT, (inventory, player) ->
                    InventoryDefinition.find(inventory)
                            .ifPresent(definition -> definition.openParent(player)))
    ),
    NEXT(head("MHF_ArrowRight", "Siguiente", "Avanza a la siguiente página.")),

    SCENARIOS(() -> InventoryDefinition.SCENARIOS, Material.CHERRY_SAPLING, "Activa, desactiva y configura los escenarios de la partida."),
    PLAYERS(() -> InventoryDefinition.PLAYERS, Material.PLAYER_HEAD, "Administra los jugadores de la partida."),
    DURATION(() -> InventoryDefinition.DURATION, Material.COMPARATOR, "Configura las opciones generales de la partida."),
    VANILLA_CHANGES(() -> InventoryDefinition.VANILLA_CHANGES, Material.GRASS_BLOCK, "Consulta y configura los cambios realizados al juego base."),
    WORLD(() -> InventoryDefinition.WORLD, () -> new ItemBuilder(Material.PLAYER_HEAD).skullOwner("KEYKOTV"), "Configura la generación y los límites del mundo."),
    TEMPLATES(() -> InventoryDefinition.TEMPLATES, Material.WRITABLE_BOOK, "Administra las plantillas de configuración.");

    private final Supplier<ItemStack> factory;
    private final List<Click> clicks;

    InventoryItem(Supplier<ItemStack> factory, Click... clicks) {
        this.factory = factory;
        this.clicks = List.of(clicks);
    }

    InventoryItem(Supplier<InventoryDefinition> definition, Material material, String description) {
        this(definition, () -> new ItemBuilder(material), description);
    }

    InventoryItem(Supplier<InventoryDefinition> definition, Supplier<ItemBuilder> builder, String description) {
        this(
                () -> builder.get()
                        .name(definition.get().getTitle())
                        .lore("<gray>" + description)
                        .build(),
                click(ClickType.LEFT, (_, player) -> definition.get().openInventory(player))
        );
    }

    public ItemStack build() {
        return new ItemBuilder(factory.get())
                .setId(name())
                .build();
    }

    public boolean execute(Inventory inventory, Player player, ClickType type) {
        for (Click click : clicks) {
            if (click.type() != type) {
                continue;
            }

            click.action().execute(inventory, player);
            return true;
        }

        return false;
    }

    public static Optional<InventoryItem> find(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return Optional.empty();
        }

        return new ItemBuilder(item)
                .findId()
                .flatMap(InventoryItem::find);
    }

    public static Display display(Material material, String description) {
        return new Display(material, description);
    }

    private static Optional<InventoryItem> find(String identifier) {
        try {
            return Optional.of(valueOf(identifier));
        } catch (IllegalArgumentException _) {
            return Optional.empty();
        }
    }

    private static Supplier<ItemStack> head(String owner, String name, String description) {
        return () -> new ItemBuilder(Material.PLAYER_HEAD)
                .skullOwner(owner)
                .name(name)
                .lore("<gray>" + description)
                .build();
    }

    private static Click click(ClickType type, Action action) {
        return new Click(type, action);
    }

    public record Display(Material material, String description) {

        public ItemStack build(String name) {
            return new ItemBuilder(material)
                    .name(name)
                    .lore("<gray>" + description)
                    .build();
        }
    }

    private record Click(ClickType type, Action action) {}

    @FunctionalInterface
    private interface Action {
        void execute(Inventory inventory, Player player);
    }
}