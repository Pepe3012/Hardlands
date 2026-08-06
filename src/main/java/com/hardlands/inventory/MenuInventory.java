package com.hardlands.inventory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class MenuInventory<T> implements InventoryHolder {

    @Getter private final T menu;
    @Getter private final Inventory inventory;
    @Getter private final @Nullable MenuInventory<T> previous;
    private final @Nullable Sound openingSound;

    public MenuInventory(T menu, Size size, Component title, Style style, @Nullable MenuInventory<T> previous) {
        this.menu = menu;
        this.previous = previous;
        this.openingSound = style.sound();
        this.inventory = Bukkit.createInventory(this, size.slots, title);

        this.renderOutline(style.outline());
    }

    public void openInventory(Player player) {
        player.openInventory(this.inventory);
        if (this.openingSound != null) {
            player.playSound(player, this.openingSound, SoundCategory.UI, 0.75F, 1.25F);
        }
    }

    public void setItem(int slot, @Nullable ItemStack item) {
        this.inventory.setItem(slot, item);
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

    public record Style(Material outline, @Nullable Sound sound) {

        public Style(Material outline) {
            this(outline, null);
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
    }
}