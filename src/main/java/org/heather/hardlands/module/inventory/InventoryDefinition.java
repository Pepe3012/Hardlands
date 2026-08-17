package org.heather.hardlands.module.inventory;

import org.heather.hardlands.common.item.InventoryItem;
import org.heather.hardlands.common.item.ItemBuilder;
import org.heather.hardlands.module.inventory.handler.InventoryHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public enum InventoryDefinition {

    MAIN(
            "Hardlands",
            Material.RED_STAINED_GLASS_PANE,
            new Layout("""
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
            )),
            null
    ),

    SCENARIOS("Escenarios", Material.PINK_STAINED_GLASS_PANE, MAIN),
    PLAYERS("Jugadores", Material.YELLOW_STAINED_GLASS_PANE, MAIN),
    WORLD("Mundo", Material.LIGHT_BLUE_STAINED_GLASS_PANE, MAIN),
    DURATION("Duración", Material.ORANGE_STAINED_GLASS_PANE, MAIN),
    TEMPLATES("Plantillas", Material.PURPLE_STAINED_GLASS_PANE, MAIN),
    VANILLA_CHANGES("Cambios de Vanilla", Material.LIME_STAINED_GLASS_PANE, MAIN);

    private static final int COLUMNS = 9;

    private final String title;
    private final Material outlineMaterial;
    private final Layout layout;
    private final InventoryHandler handler;
    private final InventoryDefinition parent;

    InventoryDefinition(String title, Material outlineMaterial, Layout layout, InventoryHandler handler, InventoryDefinition parent) {
        this.title = title;
        this.outlineMaterial = outlineMaterial;
        this.layout = layout;
        this.handler = handler;
        this.parent = parent;
    }

    InventoryDefinition(String title, Material outlineMaterial, InventoryDefinition parent) {
        this(title, outlineMaterial, Layout.BLANK, InventoryHandler.EMPTY, parent);
    }

    InventoryDefinition(String title, Material outlineMaterial, Layout layout, InventoryDefinition parent) {
        this(title, outlineMaterial, layout, InventoryHandler.EMPTY, parent);
    }

    public String getTitle() {
        return title;
    }

    public InventoryDefinition getParent() {
        return parent;
    }

    public void openInventory(Player player) {
        var inventory = InventoryRegistry.get(this);
        player.openInventory(inventory);
        handler.onOpen(inventory, player);
    }

    public void openParent(Player player) {
        if (parent != null) {
            parent.openInventory(player);
        }
    }

    public void handleClose(Inventory inventory, Player player) {
        handler.onClose(inventory, player);
    }

    public static Optional<InventoryDefinition> find(Inventory inventory) {
        if (inventory.getHolder() instanceof DefinitionHolder holder) {
            return Optional.of(holder.definition);
        }
        return Optional.empty();
    }

    Inventory createInventory() {
        var holder = new DefinitionHolder(this);
        var inventory = Bukkit.createInventory(holder, layout.getInventorySize(), Component.text(title));

        holder.inventory = inventory;

        renderFrame(inventory);
        layout.render(inventory);
        handler.onCreate(inventory);

        return inventory;
    }

    private void renderFrame(Inventory inventory) {
        var rows = inventory.getSize() / COLUMNS;
        var outline = new ItemBuilder(outlineMaterial).name("").build();

        for (var column = 1; column <= COLUMNS; column++) {
            inventory.setItem(slot(1, column), outline);
            inventory.setItem(slot(rows, column), outline);
        }

        for (var row = 2; row < rows; row++) {
            inventory.setItem(slot(row, 1), outline);
            inventory.setItem(slot(row, COLUMNS), outline);
        }

        inventory.setItem(slot(rows, 4), InventoryItem.PREVIOUS.build());
        inventory.setItem(slot(rows, 5), InventoryItem.PREPARATION.build());
        inventory.setItem(slot(rows, 6), InventoryItem.NEXT.build());
    }

    private static int slot(int row, int column) {
        return (row - 1) * COLUMNS + column - 1;
    }

    private static final class DefinitionHolder implements InventoryHolder {

        private final InventoryDefinition definition;
        private Inventory inventory;

        private DefinitionHolder(InventoryDefinition definition) {
            this.definition = definition;
        }

        @Override
        public @NonNull Inventory getInventory() {
            return inventory;
        }
    }

    private record Layout(List<String> rows, Map<Character, Supplier<InventoryItem>> items) {

        private static final char EMPTY = '-';
        private static final int CONTENT_COLUMNS = 7;
        private static final Layout BLANK = new Layout("""
                -------
                -------
                -------
                -------
                """, Map.of());

        private Layout(String layout, Map<Character, Supplier<InventoryItem>> items) {
            this(layout.strip().lines().toList(), items);
            for (var row : this.rows) {
                if (row.length() != CONTENT_COLUMNS) {
                    throw new IllegalArgumentException("Layout rows must contain exactly " + CONTENT_COLUMNS + " columns");
                }
            }
        }

        private int getInventorySize() {
            return (this.rows.size() + 2) * COLUMNS;
        }

        private void render(Inventory inventory) {
            for (var row = 0; row < this.rows.size(); row++) {
                var line = this.rows.get(row);
                for (var column = 0; column < CONTENT_COLUMNS; column++) {
                    var symbol = line.charAt(column);
                    if (symbol == EMPTY) continue;

                    var item = this.items.get(symbol);
                    if (item == null) {
                        throw new IllegalStateException("Unmapped layout symbol: '" + symbol + "'");
                    }

                    inventory.setItem(slot(row + 2, column + 2), item.get().build());
                }
            }
        }
    }
}