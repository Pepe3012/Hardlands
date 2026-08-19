package org.heather.hardlands.inventory;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.heather.hardlands.Hardlands;
import org.heather.hardlands.item.ItemBuilder;
import org.heather.hardlands.item.inventory.InventoryItem;
import org.heather.hardlands.item.inventory.PreparationItem;
import org.heather.hardlands.inventory.handler.InventoryHandler;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public enum InventoryDefinition {

    MAIN("Hardlands", Material.RED_STAINED_GLASS_PANE, new Layout("""
        -------
        -SPDWV-
        ---T---
        -------
        """, Map.of(
            'S', () -> InventoryItem.SCENARIOS,
            'P', () -> InventoryItem.PLAYERS,
            'D', () -> InventoryItem.DURATION,
            'W', () -> InventoryItem.WORLD,
            'V', () -> InventoryItem.VANILLA_CHANGES,
            'T', () -> InventoryItem.TEMPLATES
    )), null),

    SCENARIOS("Escenarios", Material.PINK_STAINED_GLASS_PANE, MAIN),
    PLAYERS("Jugadores", Material.YELLOW_STAINED_GLASS_PANE, MAIN),
    DURATION("Duración", Material.ORANGE_STAINED_GLASS_PANE, MAIN),
    WORLD("Mundo", Material.LIGHT_BLUE_STAINED_GLASS_PANE, MAIN),
    VANILLA_CHANGES("Cambios de Vanilla", Material.LIME_STAINED_GLASS_PANE, MAIN),
    TEMPLATES("Plantillas", Material.PURPLE_STAINED_GLASS_PANE, MAIN);

    private final String title;
    private final Material outline;
    private final Layout layout;
    private final InventoryHandler handler;
    private final InventoryDefinition parent;

    InventoryDefinition(String title, Material outline, Layout layout, InventoryHandler handler, InventoryDefinition parent) {
        this.title = title;
        this.outline = outline;
        this.layout = layout;
        this.handler = handler;
        this.parent = parent;
    }

    InventoryDefinition(String title, Material outline, InventoryDefinition parent) {
        this(title, outline, Layout.BLANK, InventoryHandler.EMPTY, parent);
    }

    InventoryDefinition(String title, Material outline, Layout layout, InventoryDefinition parent) {
        this(title, outline, layout, InventoryHandler.EMPTY, parent);
    }

    private static final int ROW_SIZE = 9;
    private static final int PREVIOUS_COLUMN = 4;
    private static final int PREPARATION_COLUMN = 5;
    private static final int NEXT_COLUMN = 6;

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

    public static Optional<InventoryDefinition> find(Inventory inventory) {
        if (inventory.getHolder() instanceof DefinitionHolder holder) {
            return Optional.of(holder.definition);
        }

        return Optional.empty();
    }

    Inventory createInventory() {
        DefinitionHolder holder = new DefinitionHolder(this);
        Inventory inventory = Bukkit.createInventory(
                holder,
                this.layout.getSize(),
                Component.text(this.title)
        );

        holder.inventory = inventory;

        this.renderFrame(inventory);
        this.layout.render(inventory);
        this.handler.onCreate(inventory);

        return inventory;
    }

    private void renderFrame(Inventory inventory) {
        int rows = inventory.getSize() / ROW_SIZE;
        ItemStack outline = new ItemBuilder(this.outline)
                .name("")
                .build();

        for (int column = 1; column <= ROW_SIZE; column++) {
            inventory.setItem(slot(1, column), outline);
            inventory.setItem(slot(rows, column), outline);
        }

        for (int row = 2; row < rows; row++) {
            inventory.setItem(slot(row, 1), outline);
            inventory.setItem(slot(row, ROW_SIZE), outline);
        }

        inventory.setItem(slot(rows, PREVIOUS_COLUMN), InventoryItem.PREVIOUS.build());
        inventory.setItem(slot(rows, NEXT_COLUMN), InventoryItem.NEXT.build());
    }

    private void renderPreparationItem(Inventory inventory) {
        int row = inventory.getSize() / ROW_SIZE;

        inventory.setItem(
                slot(row, PREPARATION_COLUMN),
                PreparationItem.build(Hardlands.getInstance().getWorldManagerOrThrow().getPregenerationManager())
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

    private record Layout(
            List<String> rows,
            Map<Character, Supplier<InventoryItem>> items
    ) {

        private static final char EMPTY = '-';
        private static final int CONTENT_WIDTH = 7;

        private static final Layout BLANK = new Layout("""
                -------
                -------
                -------
                -------
                """, Map.of());

        private Layout(String layout, Map<Character, Supplier<InventoryItem>> items) {
            this(layout.strip().lines().toList(), items);

            for (String row : this.rows) {
                if (row.length() != CONTENT_WIDTH) {
                    throw new IllegalArgumentException(
                            "Layout rows must contain exactly " + CONTENT_WIDTH + " columns"
                    );
                }
            }
        }

        private int getSize() {
            return (this.rows.size() + 2) * ROW_SIZE;
        }

        private void render(Inventory inventory) {
            for (int row = 0; row < this.rows.size(); row++) {
                String line = this.rows.get(row);

                for (int column = 0; column < CONTENT_WIDTH; column++) {
                    char symbol = line.charAt(column);

                    if (symbol == EMPTY) {
                        continue;
                    }

                    Supplier<InventoryItem> item = this.items.get(symbol);

                    if (item == null) {
                        throw new IllegalStateException("Unmapped layout symbol: '" + symbol + "'");
                    }

                    inventory.setItem(slot(row + 2, column + 2), item.get().build());
                }
            }
        }
    }
}