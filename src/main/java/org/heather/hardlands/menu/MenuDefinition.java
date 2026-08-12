package org.heather.hardlands.menu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.heather.hardlands.common.item.ItemBuilder;
import org.heather.hardlands.menu.handler.InventoryEditor;
import org.heather.hardlands.menu.handler.InventoryHandler;
import org.heather.hardlands.menu.handler.InventoryHandlers;

@RequiredArgsConstructor
public enum MenuDefinition {

    MAIN(Component.text("Hardlands"), Grid.SIX_ROWS, Outline.RED, InventoryHandlers.MAIN),

    GAME(Component.text("Partida"), Grid.FOUR_ROWS, Outline.RED, InventoryHandlers.EMPTY),
    SCENARIOS(Component.text("Escenarios"), Grid.FOUR_ROWS, Outline.PINK, InventoryHandlers.EMPTY),
    PLAYERS(Component.text("Jugadores"), Grid.FIVE_ROWS, Outline.YELLOW, InventoryHandlers.EMPTY),
    WORLD(Component.text("Mundo"), Grid.THREE_ROWS, Outline.LIGHT_BLUE, InventoryHandlers.EMPTY),
    SETTINGS(Component.text("Configuración"), Grid.THREE_ROWS, Outline.ORANGE, InventoryHandlers.EMPTY),
    TEMPLATES(Component.text("Plantillas"), Grid.FOUR_ROWS, Outline.PURPLE, InventoryHandlers.EMPTY),
    VANILLA_CHANGES(Component.text("Cambios de Vanilla"), Grid.THREE_ROWS, Outline.LIME, InventoryHandlers.EMPTY);

    @Getter private final Component title;
    private final Grid grid;
    private final Outline outline;
    private final InventoryHandler handler;

    public void openInventory(Player player) {
        Inventory inventory = this.getRegisteredInventory();

        if (player.openInventory(inventory) != null) {
            this.handler.onOpen(new InventoryEditor(inventory, this.grid), player);
        }
    }

    public Inventory createInventory() {
        if (MenuRegistry.isFreezed()) {
            throw new IllegalStateException("Cannot create inventories after MenuRegistry has been frozen.");
        }

        Inventory inventory = Bukkit.createInventory(null, this.grid.getSlots(), this.title);

        this.grid.setOutline(inventory, this.outline.buildStack());
        this.handler.onCreate(new InventoryEditor(inventory, this.grid));

        return inventory;
    }

    void handleClose(Inventory inventory, Player player) {
        this.handler.onClose(new InventoryEditor(inventory, this.grid), player);
    }

    private Inventory getRegisteredInventory() {
        if (!MenuRegistry.isRegistered(this)) {
            throw new IllegalStateException("Menu is not registered: " + this.name());
        }

        return MenuRegistry.get(this);
    }

    @RequiredArgsConstructor
    public enum Grid {

        ONE_ROW(1),
        TWO_ROWS(2),
        THREE_ROWS(3),
        FOUR_ROWS(4),
        FIVE_ROWS(5),
        SIX_ROWS(6);

        private static final int COLUMNS = 9;

        @Getter private final int rows;

        public int getSlots() {
            return this.rows * COLUMNS;
        }

        public int slot(int row, int column) {
            if (row < 1 || row > this.rows) {
                throw new IllegalArgumentException("Row must be between 1 and " + this.rows + ": " + row);
            }

            if (column < 1 || column > COLUMNS) {
                throw new IllegalArgumentException("Column must be between 1 and " + COLUMNS + ": " + column);
            }

            return (row - 1) * COLUMNS + column - 1;
        }

        public void setOutline(Inventory inventory, ItemStack item) {
            for (int slot = 0; slot < this.getSlots(); slot++) {
                if (this.isOutlineSlot(slot)) {
                    inventory.setItem(slot, item);
                }
            }
        }

        private boolean isOutlineSlot(int slot) {
            int column = slot % COLUMNS;

            return slot < COLUMNS
                    || slot >= this.getSlots() - COLUMNS
                    || column == 0
                    || column == COLUMNS - 1;
        }
    }

    @RequiredArgsConstructor
    public enum Outline {

        RED(Material.RED_STAINED_GLASS_PANE),
        PINK(Material.PINK_STAINED_GLASS_PANE),
        YELLOW(Material.YELLOW_STAINED_GLASS_PANE),
        LIGHT_BLUE(Material.LIGHT_BLUE_STAINED_GLASS_PANE),
        ORANGE(Material.ORANGE_STAINED_GLASS_PANE),
        PURPLE(Material.PURPLE_STAINED_GLASS_PANE),
        LIME(Material.LIME_STAINED_GLASS_PANE);

        private final Material material;

        public ItemStack buildStack() {
            return new ItemBuilder(this.material).name("").build();
        }
    }
}