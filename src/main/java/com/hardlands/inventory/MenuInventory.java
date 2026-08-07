package com.hardlands.inventory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class MenuInventory implements InventoryHolder {

    @Getter private final Menu menu;
    @Getter private final Inventory inventory;
    @Getter @Nullable private final MenuInventory previous;
    private final Map<Integer, BiConsumer<Player, ClickType>> actions = new HashMap<>();

    public MenuInventory(Menu menu, Size size, Component title, Material outline, @Nullable MenuInventory previous) {
        this.menu = menu;
        this.inventory = Bukkit.createInventory(this, size.slots, title);
        this.previous = previous;
        this.renderOutline(outline);
    }

    public void open(Player player) {
        player.openInventory(this.inventory);
    }

    public void item(int slot, ItemStack item, Consumer<Player> action) {
        this.item(slot, item, (player, click) -> action.accept(player));
    }

    public void item(int slot, ItemStack item, BiConsumer<Player, ClickType> action) {
        this.inventory.setItem(slot, item);
        this.actions.put(slot, action);
    }

    public void setItem(int slot, @Nullable ItemStack item) {
        this.inventory.setItem(slot, item);
    }

    @Nullable BiConsumer<Player, ClickType> getAction(int slot) {
        return this.actions.get(slot);
    }

    private void renderOutline(Material material) {
        ItemStack pane = new ItemStack(material);
        pane.editMeta(meta -> meta.displayName(Component.empty()));

        int lastRow = this.inventory.getSize() / 9 - 1;
        for (int slot = 0; slot < this.inventory.getSize(); slot++) {
            int row = slot / 9;
            int column = slot % 9;
            if (row == 0 || row == lastRow || column == 0 || column == 8) {
                this.inventory.setItem(slot, pane);
            }
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
            int rowCount = this.slots / 9;

            if (row < 1 || row > rowCount) throw new IllegalArgumentException("Row " + row + " is out of bounds for " + this.name() + " (1-" + rowCount + ")");
            if (column < 0 || column > 8) throw new IllegalArgumentException("Column " + column + " is out of bounds (0-8)");

            return (row - 1) * 9 + column;
        }
    }
}