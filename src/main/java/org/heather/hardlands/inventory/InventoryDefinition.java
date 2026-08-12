package org.heather.hardlands.inventory;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.heather.hardlands.common.item.InventoryItem;
import org.heather.hardlands.common.item.ItemBuilder;
import org.heather.hardlands.inventory.handler.InventoryHandler;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public enum InventoryDefinition {

    MAIN("Hardlands", Material.RED_STAINED_GLASS_PANE, new Layout("""
        -------
        -SPWDT-
        ---V---
        -------
        """, Map.of(
            'S', () -> InventoryItem.SCENARIOS,
            'P', () -> InventoryItem.PLAYERS,
            'W', () -> InventoryItem.WORLD,
            'D', () -> InventoryItem.DURATION,
            'T', () -> InventoryItem.TEMPLATES,
            'V', () -> InventoryItem.VANILLA_CHANGES
    ))),

    SCENARIOS("Escenarios", Material.PINK_STAINED_GLASS_PANE),
    PLAYERS("Jugadores", Material.YELLOW_STAINED_GLASS_PANE),
    WORLD("Mundo", Material.LIGHT_BLUE_STAINED_GLASS_PANE),
    DURATION("Duración", Material.ORANGE_STAINED_GLASS_PANE),
    TEMPLATES("Plantillas", Material.PURPLE_STAINED_GLASS_PANE),
    VANILLA_CHANGES("Cambios de Vanilla", Material.LIME_STAINED_GLASS_PANE);

    private static final int COLUMNS = 9;

    @Getter private final String title;
    private final Material outlineMaterial;
    private final Layout layout;
    private final InventoryHandler handler;

    InventoryDefinition(String title, Material outlineMaterial) {
        this(title, outlineMaterial, Layout.BLANK, InventoryHandler.EMPTY);
    }

    InventoryDefinition(String title, Material outlineMaterial, Layout layout) {
        this(title, outlineMaterial, layout, InventoryHandler.EMPTY);
    }

    InventoryDefinition(String title, Material outlineMaterial, Layout layout, InventoryHandler handler) {
        this.title = title;
        this.outlineMaterial = outlineMaterial;
        this.layout = layout;
        this.handler = handler;
    }

    Inventory createInventory() {
        Inventory inventory = Bukkit.createInventory(null, this.layout.getInventorySize(), Component.text(this.title));

        this.renderFrame(inventory);
        this.layout.render(inventory);
        this.handler.onCreate(inventory);

        return inventory;
    }

    public void openInventory(Player player) {
        Inventory inventory = InventoryRegistry.get(this);

        if (player.openInventory(inventory) != null) {
            this.handler.onOpen(inventory, player);
        }
    }

    void handleClose(Inventory inventory, Player player) {
        this.handler.onClose(inventory, player);
    }

    private void renderFrame(Inventory inventory) {
        int rows = inventory.getSize() / COLUMNS;
        ItemStack outline = new ItemBuilder(this.outlineMaterial).name("").build();

        for (int column = 1; column <= COLUMNS; column++) {
            inventory.setItem(slot(1, column), outline.clone());
            inventory.setItem(slot(rows, column), outline.clone());
        }

        for (int row = 2; row < rows; row++) {
            inventory.setItem(slot(row, 1), outline.clone());
            inventory.setItem(slot(row, COLUMNS), outline.clone());
        }

        inventory.setItem(slot(rows, 4), InventoryItem.PREVIOUS.build());
        inventory.setItem(slot(rows, 5), InventoryItem.WORLD_STATE.build());
        inventory.setItem(slot(rows, 6), InventoryItem.NEXT.build());
    }

    private static int slot(int row, int column) {
        return (row - 1) * COLUMNS + column - 1;
    }

    private record Layout(List<String> rows, Map<Character, Supplier<InventoryItem>> items) {

        private static final Layout BLANK = new Layout("""
            -------
            -------
            -------
            -------
            """, Map.of());

        private static final char EMPTY = '-';
        private static final int COLUMNS = 7;

        private Layout(String layout, Map<Character, Supplier<InventoryItem>> items) {
            this(layout.strip().lines().toList(), items);
        }

        private int getInventorySize() {
            return (this.rows.size() + 2) * InventoryDefinition.COLUMNS;
        }

        private void render(Inventory inventory) {
            for (int row = 0; row < this.rows.size(); row++) {
                String line = this.rows.get(row);

                for (int column = 0; column < COLUMNS; column++) {
                    char symbol = line.charAt(column);
                    if (symbol == EMPTY) continue;

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