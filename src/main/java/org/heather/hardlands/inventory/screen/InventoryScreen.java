package org.heather.hardlands.inventory.screen;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.heather.hardlands.Hardlands;
import org.heather.hardlands.common.item.ItemBuilder;

public abstract class InventoryScreen {

    protected final Hardlands plugin;
    protected final Definition definition;

    protected InventoryScreen(final Hardlands plugin, Definition definition) {
        this.plugin = plugin;
        this.definition = definition;
    }

    public final void openInventory(Player player) {
        Inventory inventory = this.definition.createInventory();

        this.renderOutline(inventory);
        this.onInitialize(inventory, player);

        player.openInventory(inventory);
    }

    protected abstract void onInitialize(Inventory inventory, Player player);

    private void renderOutline(Inventory inventory) {
        ItemStack outline = this.definition.outlineType.buildStack();

        for (int slot = 0; slot < inventory.getSize(); slot++) {
            if (this.definition.gridSize.isOutlineSlot(slot)) {
                inventory.setItem(slot, outline);
            }
        }
    }

    public record Definition(String name, GridSize gridSize, OutlineType outlineType) {

        public Inventory createInventory() {
            return Bukkit.createInventory(null, this.gridSize.slots, Component.text(this.name));
        }
    }

    @Getter
    @RequiredArgsConstructor
    public enum GridSize {

        ONE_ROW(9),
        TWO_ROWS(18),
        THREE_ROWS(27),
        FOUR_ROWS(36),
        FIVE_ROWS(45),
        SIX_ROWS(54);

        private static final int COLUMNS = 9;
        private final int slots;

        public int slot(int row, int column) {
            return (row - 1) * COLUMNS + column;
        }

        public boolean isOutlineSlot(int slot) {
            int column = slot % COLUMNS;
            return slot < COLUMNS || slot >= this.slots - COLUMNS || column == 0 || column == COLUMNS - 1;
        }
    }

    @RequiredArgsConstructor
    public enum OutlineType {

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