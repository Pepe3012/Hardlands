package com.hardlands.inventory;

import com.hardlands.item.InventoryItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;

public interface Menu {

    int BACK_SLOT = 45;

    String displayName();

    Material outline();

    MenuInventory.Size size();

    @Nullable Menu parent();

    default void build(MenuInventory menu, Player player) {
    }

    default void open(Player player) {
        open(player, null);
    }

    default void open(Player player, @Nullable MenuInventory previous) {
        MenuInventory menu = new MenuInventory(this, size(), title(), outline(), previous);

        if (previous != null) {
            menu.item(BACK_SLOT, InventoryItem.PREVIOUS.getItem(), previous::open);
        }

        build(menu, player);
        menu.open(player);
    }

    private Component title() {
        Deque<String> parts = new ArrayDeque<>();

        for (Menu menu = this; menu != null; menu = menu.parent()) {
            if (!menu.displayName().isEmpty()) parts.addFirst(menu.displayName());
        }

        return Component.text(String.join(" » ", parts));
    }
}