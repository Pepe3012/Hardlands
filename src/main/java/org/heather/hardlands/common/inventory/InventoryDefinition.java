package org.heather.hardlands.common.inventory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.heather.hardlands.common.inventory.handler.InventoryHandler;
import org.heather.hardlands.common.item.InventoryItem;
import org.heather.hardlands.common.item.ItemBuilder;
import org.jspecify.annotations.NonNull;

public enum InventoryDefinition {

    MAIN("Hardlands", Outline.RED, new Layout("""
                -------
                -SPGFW-
                ---T---
                -------
                """, Map.of(
            'S', InventoryItem.SCENARIOS,
            'P', InventoryItem.PLAYERS,
            'G', InventoryItem.GENERAL,
            'F', InventoryItem.PHASES,
            'W', InventoryItem.WORLD,
            'T', InventoryItem.PRESETS))),

    SCENARIOS("Escenarios", Outline.PINK),
    PLAYERS("Jugadores", Outline.YELLOW),
    GENERAL("General", Outline.ORANGE),
    PHASES("Fases", Outline.LIME),
    WORLD("Mundo", Outline.LIGHT_BLUE),
    PRESETS("Plantillas", Outline.PURPLE);

    private static final int ROW_SIZE = 9;
    private static final int CONTENT_WIDTH = ROW_SIZE - 2;

    private final String title;
    private final ItemStack outline;
    private final Layout layout;
    private final InventoryHandler handler;

    InventoryDefinition(String title, Outline outline, Layout layout, InventoryHandler handler) {
        this.title = title;
        this.outline = outline.buildItem();
        this.layout = layout;
        this.handler = handler;
    }

    InventoryDefinition(String title, Outline outline, Layout layout) {
        this(title, outline, layout, InventoryHandler.EMPTY);
    }

    InventoryDefinition(String title, Outline outline) {
        this(title, outline, Layout.BLANK);
    }

    public void openInventory(Player player) {
        Inventory inventory = InventoryRegistry.getInventory(this);

        this.renderFooter(inventory);

        player.openInventory(inventory);
        this.handler.onOpen(inventory, player);
    }

    public void handleClose(Inventory inventory, Player player) {
        this.handler.onClose(inventory, player);
    }

    Inventory createInventory() {
        DefinitionHolder holder = new DefinitionHolder(this);
        Inventory inventory = Bukkit.createInventory(holder, this.layout.getSize(), Component.text(this.title));

        holder.setInventory(inventory);

        this.renderFrame(inventory);
        this.layout.render(inventory);
        this.handler.onCreate(inventory);

        return inventory;
    }

    private void renderFrame(Inventory inventory) {
        int bottomRow = getBottomRow(inventory);

        for (int column = 1; column <= ROW_SIZE; column++) {
            inventory.setItem(slot(1, column), this.outline);
            inventory.setItem(slot(bottomRow, column), this.outline);
        }

        for (int row = 2; row < bottomRow; row++) {
            inventory.setItem(slot(row, 1), this.outline);
            inventory.setItem(slot(row, ROW_SIZE), this.outline);
        }
    }

    private void renderFooter(Inventory inventory) {
        int bottomRow = getBottomRow(inventory);

        inventory.setItem(slot(bottomRow, 4), InventoryItem.PREVIOUS.buildItem());
        inventory.setItem(slot(bottomRow, 5), InventoryItem.PREPARATION.buildItem());
        inventory.setItem(slot(bottomRow, 6), InventoryItem.NEXT.buildItem());
    }

    //
    public static Optional<InventoryDefinition> findDefinition(Inventory inventory) {
        return inventory.getHolder() instanceof DefinitionHolder holder
                ? Optional.of(holder.definition)
                : Optional.empty();
    }

    private static int getBottomRow(Inventory inventory) {
        return inventory.getSize() / ROW_SIZE;
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

        private void setInventory(Inventory inventory) {
            this.inventory = inventory;
        }
    }

    private record Outline(Material material) {

        private static final Outline RED = new Outline(Material.RED_STAINED_GLASS_PANE);
        private static final Outline PINK = new Outline(Material.PINK_STAINED_GLASS_PANE);
        private static final Outline YELLOW = new Outline(Material.YELLOW_STAINED_GLASS_PANE);
        private static final Outline ORANGE = new Outline(Material.ORANGE_STAINED_GLASS_PANE);
        private static final Outline LIME = new Outline(Material.LIME_STAINED_GLASS_PANE);
        private static final Outline LIGHT_BLUE = new Outline(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        private static final Outline PURPLE = new Outline(Material.PURPLE_STAINED_GLASS_PANE);

        private ItemStack buildItem() {
            return new ItemBuilder(this.material)
                    .name("")
                    .build();
        }
    }

    private record Layout(
            List<String> rows,
            Map<Character, InventoryItem> items) {

        private static final Layout BLANK = new Layout("""
                -------
                -------
                -------
                -------
                """, Map.of());

        private Layout(String layout, Map<Character, InventoryItem> items) {
            this(layout.strip().lines().toList(), items);
        }

        private void render(Inventory inventory) {
            for (int row = 0; row < this.rows.size(); row++) {
                String line = this.rows.get(row);

                for (int column = 0; column < CONTENT_WIDTH; column++) {
                    char symbol = line.charAt(column);

                    if (symbol == '-') {
                        continue;
                    }

                    InventoryItem item = this.items.get(symbol);

                    if (item == null) {
                        throw new IllegalStateException(
                                "No InventoryItem defined for layout symbol '%s'.".formatted(symbol));
                    }

                    inventory.setItem(slot(row + 2, column + 2), item.buildItem());
                }
            }
        }

        private int getSize() {
            return (this.rows.size() + 2) * ROW_SIZE;
        }
    }
}