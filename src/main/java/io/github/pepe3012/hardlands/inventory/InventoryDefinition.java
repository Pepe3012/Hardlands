package io.github.pepe3012.hardlands.inventory;

import io.github.pepe3012.hardlands.common.item.ItemBuilder;
import io.github.pepe3012.hardlands.common.item.InventoryItem;
import io.github.pepe3012.hardlands.inventory.handler.InventoryHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
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
    WORLD("Mundo", Material.LIGHT_BLUE_STAINED_GLASS_PANE, MAIN),
    DURATION("Duración", Material.ORANGE_STAINED_GLASS_PANE, MAIN),
    TEMPLATES("Plantillas", Material.PURPLE_STAINED_GLASS_PANE, MAIN),
    VANILLA_CHANGES("Cambios de Vanilla", Material.LIME_STAINED_GLASS_PANE, MAIN);

    private final String title;
    private final Material outlineMaterial;
    private final Layout layout;
    private final InventoryHandler handler;
    private final InventoryDefinition parent;

    InventoryDefinition(String title, Material outlineMaterial, InventoryDefinition parent) {
        this(title, outlineMaterial, Layout.BLANK, InventoryHandler.EMPTY, parent);
    }

    InventoryDefinition(String title, Material outlineMaterial, Layout layout, InventoryDefinition parent) {
        this(title, outlineMaterial, layout, InventoryHandler.EMPTY, parent);
    }

    InventoryDefinition(String title, Material outlineMaterial, Layout layout, InventoryHandler handler, InventoryDefinition parent) {
        this.title = title;
        this.outlineMaterial = outlineMaterial;
        this.layout = layout;
        this.handler = handler;
        this.parent = parent;
    }

    private static final int COLUMNS = 9;

    public String getTitle() {
        return this.title;
    }

    public InventoryDefinition getParent() {
        return this.parent;
    }

    Inventory createInventory() {
        DefinitionHolder holder = new DefinitionHolder(this);
        Inventory inventory = Bukkit.createInventory(holder, this.layout.getInventorySize(), Component.text(this.title));

        holder.setInventory(inventory);

        this.renderFrame(inventory);
        this.layout.render(inventory);
        this.handler.onCreate(inventory);

        return inventory;
    }

    public void openInventory(Player player) {
        Inventory inventory = InventoryRegistry.get(this);

        player.openInventory(inventory);
        this.handler.onOpen(inventory, player);
    }

    public void openParent(Player player) {
        if (this.parent != null) {
            this.parent.openInventory(player);
        }
    }

    public static Optional<InventoryDefinition> find(Inventory inventory) {
        if (inventory.getHolder() instanceof DefinitionHolder holder) {
            return Optional.of(holder.getDefinition());
        }
        return Optional.empty();
    }

    void handleClose(Inventory inventory, Player player) {
        this.handler.onClose(inventory, player);
    }

    private void renderFrame(Inventory inventory) {
        int rows = inventory.getSize() / COLUMNS;
        ItemStack outline = new ItemBuilder(this.outlineMaterial).name("").build();

        for (int column = 1; column <= COLUMNS; column++) {
            inventory.setItem(slot(1, column), outline);
            inventory.setItem(slot(rows, column), outline);
        }

        for (int row = 2; row < rows; row++) {
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

        private InventoryDefinition getDefinition() {
            return this.definition;
        }

        private void setInventory(Inventory inventory) {
            this.inventory = inventory;
        }

        @Override
        public @NonNull Inventory getInventory() {
            return this.inventory;
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
            for (String row : this.rows) {
                if (row.length() != CONTENT_COLUMNS) {
                    throw new IllegalArgumentException("Layout rows must contain exactly " + CONTENT_COLUMNS + " columns");
                }
            }
        }

        private int getInventorySize() {
            return (this.rows.size() + 2) * InventoryDefinition.COLUMNS;
        }

        private void render(Inventory inventory) {
            for (int row = 0; row < this.rows.size(); row++) {
                String line = this.rows.get(row);

                for (int column = 0; column < CONTENT_COLUMNS; column++) {
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