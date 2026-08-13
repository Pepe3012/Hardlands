package io.github.pepe3012.hardlands.common.item.inventory;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import io.github.pepe3012.hardlands.Hardlands;
import io.github.pepe3012.hardlands.common.item.ItemBuilder;
import io.github.pepe3012.hardlands.config.inventory.InventoryDefinition;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public enum InventoryItem {

    PREVIOUS(item(
            head("MHF_ArrowLeft", "Anterior", "Regresa al menú o página anterior."),
            click(ClickType.LEFT, (inventory, player) -> InventoryDefinition.find(inventory).ifPresent(definition -> definition.openParent(player)))
    )),

    PREPARATION(item(
            PreparationInventoryItem::build,
            click(ClickType.RIGHT, (_, _) -> PreparationInventoryItem.toggle())
    )),

    NEXT(item(head("MHF_ArrowRight", "Siguiente", "Avanza a la siguiente página."))),

    SCENARIOS(menu(() -> InventoryDefinition.SCENARIOS, Material.CHERRY_SAPLING, "Activa, desactiva y configura los escenarios de la partida.")),
    PLAYERS(menu(() -> InventoryDefinition.PLAYERS, Material.PLAYER_HEAD, "Administra los jugadores de la partida.")),
    DURATION(menu(() -> InventoryDefinition.DURATION, Material.COMPARATOR, "Configura las opciones generales de la partida.")),
    VANILLA_CHANGES(menu(() -> InventoryDefinition.VANILLA_CHANGES, Material.GRASS_BLOCK, "Consulta y configura los cambios realizados al juego base.")),
    WORLD(menu(() -> InventoryDefinition.WORLD, () -> new ItemBuilder(Material.PLAYER_HEAD).skullOwner("KEYKOTV"), "Configura la generación y los límites del mundo.")),
    TEMPLATES(menu(() -> InventoryDefinition.TEMPLATES, Material.WRITABLE_BOOK, "Administra las plantillas de configuración."));

    private final Supplier<ItemStack> factory;
    private final List<InventoryItemClick> clicks;

    InventoryItem(InventoryItemConfiguration configuration) {
        this.factory = configuration.factory();
        this.clicks = configuration.clicks();
    }

    public ItemStack build() {
        ItemStack item = this.factory.get();

        item.editMeta(meta -> meta.getPersistentDataContainer()
                .set(itemKey(), PersistentDataType.STRING, this.name()));

        return item;
    }

    public boolean execute(Inventory inventory, Player player, ClickType clickType) {
        return this.clicks.stream()
                .filter(click -> click.matches(clickType))
                .findFirst()
                .map(click -> {
                    click.execute(inventory, player);
                    return true;
                })
                .orElse(false);
    }

    public static Optional<InventoryItem> find(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return Optional.empty();

        String identifier = item.getItemMeta()
                .getPersistentDataContainer()
                .get(itemKey(), PersistentDataType.STRING);

        if (identifier == null) return Optional.empty();

        try {
            return Optional.of(valueOf(identifier));
        } catch (IllegalArgumentException _) {
            return Optional.empty();
        }
    }

    public static InventoryDisplay display(Material material, String description) {
        return new InventoryDisplay(material, description);
    }

    private static InventoryItemConfiguration item(
            Supplier<ItemStack> factory,
            InventoryItemClick... clicks
    ) {
        return new InventoryItemConfiguration(factory, List.of(clicks));
    }

    private static InventoryItemConfiguration menu(
            Supplier<InventoryDefinition> definition,
            Material material,
            String description
    ) {
        return menu(definition, () -> new ItemBuilder(material), description);
    }

    private static InventoryItemConfiguration menu(
            Supplier<InventoryDefinition> definition,
            Supplier<ItemBuilder> builder,
            String description
    ) {
        return item(
                () -> InventoryItemFactory.menu(definition.get(), builder.get(), description),
                click(ClickType.LEFT, (_, player) -> definition.get().openInventory(player))
        );
    }

    private static Supplier<ItemStack> head(String owner, String name, String description) {
        return () -> InventoryItemFactory.head(owner, name, description);
    }

    private static InventoryItemClick click(
            ClickType clickType,
            InventoryItemClick.Action action
    ) {
        return new InventoryItemClick(clickType, action);
    }

    private static NamespacedKey itemKey() {
        return Hardlands.getInstance().namespacedKey("INVENTORY_ITEM");
    }
}