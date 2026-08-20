package org.heather.hardlands.common.inventory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.heather.hardlands.Hardlands;
import org.heather.hardlands.common.inventory.handler.InventoryHandler;
import org.heather.hardlands.common.item.ItemBuilder;
import org.heather.hardlands.common.item.inventory.InventoryItem;
import org.heather.hardlands.common.item.inventory.PreparationItem;
import org.jspecify.annotations.NonNull;

public enum InventoryDefinition {
    MAIN(
            "Hardlands",
            Outline.RED,
            new Layout(
                    """
            -------
            -SPDWV-
            ---T---
            -------
            """,
                    Map.of('S', () -> InventoryItem.SCENARIOS, 'P', () -> InventoryItem.PLAYERS, 'D', () -> InventoryItem.DURATION, 'W', () -> InventoryItem.WORLD, 'V', () -> InventoryItem.VANILLA_CHANGES, 'T', () -> InventoryItem.TEMPLATES)
            ),
            null
    ),
    SCENARIOS("Escenarios", Outline.PINK, MAIN),
    PLAYERS("Jugadores", Outline.YELLOW, MAIN),
    DURATION("Duración", Outline.ORANGE, MAIN),
    WORLD("Mundo", Outline.LIGHT_BLUE, MAIN),
    VANILLA_CHANGES("Cambios de Vanilla", Outline.LIME, MAIN),
    TEMPLATES("Plantillas", Outline.PURPLE, MAIN);
    private static final int ROW_SIZE = 9;
    private static final int PREVIOUS_COLUMN = 4;
    private static final int PREPARATION_COLUMN = 5;
    private static final int NEXT_COLUMN = 6;
    private final String title;
    private final ItemStack outline;
    private final Layout layout;
    private final InventoryHandler handler;
    private final InventoryDefinition parent;

    InventoryDefinition(
            String title,
            Outline outline,
            Layout layout,
            InventoryHandler handler,
            InventoryDefinition parent
    ) {
        this.title = title;
        this.outline = outline.build();
        this.layout = layout;
        this.handler = handler;
        this.parent = parent;
    }

    InventoryDefinition(String title, Outline outline, Layout layout, InventoryDefinition parent) {
        this(title, outline, layout, InventoryHandler.EMPTY, parent);
    }

    InventoryDefinition(String title, Outline outline, InventoryDefinition parent) {
        this(title, outline, Layout.BLANK, InventoryHandler.EMPTY, parent);
    }

    public static Optional<InventoryDefinition> findDefinition(Inventory inventory) {
        if (inventory.getHolder() instanceof DefinitionHolder definitionHolder) {
            return Optional.of(definitionHolder.definition);
        }

        return Optional.empty();
    }

    public String getTitle() {
        return this.title;
    }

    public InventoryDefinition getParent() {
        return this.parent;
    }

    public void openInventory(Player player) {
        Inventory inventory = InventoryRegistry.getInventory(this);

        this.renderPreparationItem(inventory);

        player.openInventory(inventory);
        this.handler.onOpen(inventory, player);
    }

    public void openParent(Player player) {
        if (this.parent != null) {
            this.parent.openInventory(player);
        }
    }

    public void handleClose(Inventory inventory, Player player) {
        this.handler.onClose(inventory, player);
    }

    Inventory createInventory() {
        DefinitionHolder holder = new DefinitionHolder(this);
        Inventory inventory = Bukkit.createInventory(holder, this.layout.getSize(), Component.text(this.title));

        holder.inventory = inventory;

        this.renderFrame(inventory);
        this.layout.render(inventory);
        this.handler.onCreate(inventory);

        return inventory;
    }

    private void renderFrame(Inventory inventory) {
        int rows = inventory.getSize() / ROW_SIZE;

        for (int column = 1; column <= ROW_SIZE; column++) {
            inventory.setItem(slot(1, column), this.outline);
            inventory.setItem(slot(rows, column), this.outline);
        }

        for (int row = 2; row < rows; row++) {
            inventory.setItem(slot(row, 1), this.outline);
            inventory.setItem(slot(row, ROW_SIZE), this.outline);
        }

        inventory.setItem(slot(rows, PREVIOUS_COLUMN), InventoryItem.PREVIOUS.build());
        inventory.setItem(slot(rows, NEXT_COLUMN), InventoryItem.NEXT.build());
    }

    private void renderPreparationItem(Inventory inventory) {
        inventory.setItem(
                slot(inventory.getSize() / ROW_SIZE, PREPARATION_COLUMN),
                PreparationItem.build(Hardlands.getInstance().getWorldManagerOrThrow())
        );
    }

    private static int slot(int row, int column) {
        return (row - 1) * ROW_SIZE + column - 1;
    }

    private static final class DefinitionHolder implements InventoryHolder {
        private final InventoryDefinition definition;
        private Inventory inventory;

        private DefinitionHolder(InventoryDefinition definition) {
            this.definition = definition;
        }

        @Override
        public @NonNull Inventory getInventory() {
            return this.inventory;
        }
    }

    private record Outline(Material material) {
        public static final Outline RED = new Outline(Material.RED_STAINED_GLASS_PANE);
        public static final Outline PINK = new Outline(Material.PINK_STAINED_GLASS_PANE);
        public static final Outline YELLOW = new Outline(Material.YELLOW_STAINED_GLASS_PANE);
        public static final Outline ORANGE = new Outline(Material.ORANGE_STAINED_GLASS_PANE);
        public static final Outline LIGHT_BLUE = new Outline(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        public static final Outline LIME = new Outline(Material.LIME_STAINED_GLASS_PANE);
        public static final Outline PURPLE = new Outline(Material.PURPLE_STAINED_GLASS_PANE);

        public ItemStack build() {
            return new ItemBuilder(this.material).name("").build();
        }
    }

    private record Layout(List<String> rows, Map<Character, Supplier<InventoryItem>> items) {
        private static final Layout BLANK =
                new Layout("""
                -------
                -------
                -------
                -------
                """, Map.of());

        private Layout(String layout, Map<Character, Supplier<InventoryItem>> items) {
            this(layout.strip().lines().toList(), items);
        }

        private int getSize() {
            return (this.rows.size() + 2) * ROW_SIZE;
        }

        private void render(Inventory inventory) {
            for (int row = 0; row < this.rows.size(); row++) {
                String line = this.rows.get(row);

                for (int column = 0; column < 7; column++) {
                    char symbol = line.charAt(column);
                    if (symbol == '-') {
                        continue;
                    }

                    inventory.setItem(slot(row + 2, column + 2), this.items.get(symbol).get().build());
                }
            }
        }
    }
}
