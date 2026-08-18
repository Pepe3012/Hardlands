package org.heather.hardlands.common.item.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.heather.hardlands.common.item.ItemBuilder;
import org.heather.hardlands.module.inventory.InventoryDefinition;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public enum InventoryItem {

    PREVIOUS(
            head("MHF_ArrowLeft", "Anterior", "Regresa al menú o página anterior."),
            click(ClickType.LEFT, (inventory, player) -> InventoryDefinition.find(inventory).ifPresent(definition -> definition.openParent(player)))
    ),
    NEXT(
            head("MHF_ArrowRight", "Siguiente", "Avanza a la siguiente página.")
    ),

    SCENARIOS(() -> InventoryDefinition.SCENARIOS, Material.CHERRY_SAPLING, "Activa, desactiva y configura los escenarios de la partida."),
    PLAYERS(() -> InventoryDefinition.PLAYERS, Material.PLAYER_HEAD, "Administra los jugadores de la partida."),
    DURATION(() -> InventoryDefinition.DURATION, Material.COMPARATOR, "Configura las opciones generales de la partida."),
    VANILLA_CHANGES(() -> InventoryDefinition.VANILLA_CHANGES, Material.GRASS_BLOCK, "Consulta y configura los cambios realizados al juego base."),
    WORLD(() -> InventoryDefinition.WORLD, () -> new ItemBuilder(Material.PLAYER_HEAD).skullOwner("KEYKOTV"), "Configura la generación y los límites del mundo."),
    TEMPLATES(() -> InventoryDefinition.TEMPLATES, Material.WRITABLE_BOOK, "Administra las plantillas de configuración.");

    private final Supplier<ItemStack> itemFactory;
    private final List<ClickBinding> clickBindings;

    InventoryItem(Supplier<ItemStack> factory, ClickBinding... bindings) {
        this.itemFactory = factory;
        this.clickBindings = List.of(bindings);
    }

    InventoryItem(Supplier<InventoryDefinition> definitionFactory, Supplier<ItemBuilder> builderFactory, String description) {
        this(
                () -> builderFactory.get()
                        .name(definitionFactory.get().getTitle())
                        .lore("<gray>" + description)
                        .build(),
                click(ClickType.LEFT, (_, player) -> definitionFactory.get().openInventory(player))
        );
    }

    InventoryItem(Supplier<InventoryDefinition> definitionFactory, Material material, String description) {
        this(definitionFactory, () -> new ItemBuilder(material), description);
    }

    public ItemStack build() {
        return new ItemBuilder(this.itemFactory.get())
                .setId(this.name())
                .build();
    }

    public boolean execute(Inventory inventory, Player player, ClickType type) {
        for (ClickBinding binding : this.clickBindings) {
            if (binding.type() == type) {
                binding.action().execute(inventory, player);
                return true;
            }
        }

        return false;
    }

    public static Optional<InventoryItem> find(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return Optional.empty();
        }

        return new ItemBuilder(item)
                .findId()
                .flatMap(InventoryItem::findById);
    }

    public static Display display(Material material, String description) {
        return new Display(material, description);
    }

    private static Optional<InventoryItem> findById(String identifier) {
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

    private static ClickBinding click(ClickType type, ClickAction action) {
        return new ClickBinding(type, action);
    }

    public record Display(Material material, String description) {

        public ItemStack build(String name) {
            return new ItemBuilder(this.material)
                    .name(name)
                    .lore("<gray>" + this.description)
                    .build();
        }
    }

    private record ClickBinding(ClickType type, ClickAction action) {}

    @FunctionalInterface
    private interface ClickAction {

        void execute(Inventory inventory, Player player);
    }
}