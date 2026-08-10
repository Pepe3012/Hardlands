package com.hardlands.common.menu;

import com.hardlands.common.item.ItemBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class MenuInventory implements InventoryHolder {

    private static final int ROW_SIZE = 9;

    @Getter private final Menu menu;
    @Getter private final Inventory inventory;
    @Getter private final Size size;
    @Getter @Nullable private final MenuInventory previous;

    private final Map<Integer, MenuAction> actions = new HashMap<>();

    public MenuInventory(Menu menu, Size size, Component title, Material outline, @Nullable MenuInventory previous) {
        this.menu = menu;
        this.size = size;
        this.inventory = Bukkit.createInventory(this, size.slots, title);
        this.previous = previous;
        this.renderOutline(outline);
    }

    public void open(Player player) {
        player.openInventory(this.inventory);
    }

    public void item(int slot, ItemStack item) {
        this.setItem(slot, item);
    }

    public void item(int slot, ItemStack item, @Nullable MenuAction action) {
        if (action == null) {
            this.setItem(slot, item);
            return;
        }

        ItemBuilder builder = new ItemBuilder(item);

        if (!action.lore().isEmpty()) {
            builder.addLore("");
            builder.addLore(action.lore().toArray(String[]::new));
        }

        this.inventory.setItem(slot, builder.build());
        this.actions.put(slot, action);
    }

    public void setItem(int slot, @Nullable ItemStack item) {
        this.inventory.setItem(slot, item);
        this.actions.remove(slot);
    }

    @Nullable
    MenuAction getAction(int slot) {
        return this.actions.get(slot);
    }

    private void renderOutline(Material material) {
        ItemStack pane = new ItemStack(material);
        pane.editMeta(meta -> meta.displayName(Component.empty()));

        int size = this.inventory.getSize();

        for (int slot = 0; slot < size; slot++) {
            int column = slot % ROW_SIZE;
            if (slot < ROW_SIZE || slot >= size - ROW_SIZE || column == 0 || column == ROW_SIZE - 1) this.inventory.setItem(slot, pane);
        }
    }

    @RequiredArgsConstructor
    public enum Size {

        ONE_ROW(9),
        TWO_ROWS(18),
        THREE_ROWS(27),
        FOUR_ROWS(36),
        FIVE_ROWS(45),
        SIX_ROWS(54);

        private final int slots;

        public int slot(int row, int column) {
            int rows = this.slots / ROW_SIZE;
            if (row < 1 || row > rows) throw new IllegalArgumentException("Row " + row + " is out of bounds for " + this.name() + " (1-" + rows + ")");
            if (column < 0 || column >= ROW_SIZE) throw new IllegalArgumentException("Column " + column + " is out of bounds (0-" + (ROW_SIZE - 1) + ")");
            return (row - 1) * ROW_SIZE + column;
        }
    }
}