package com.hardlands.player;

import com.hardlands.HardlandsPlugin;
import com.hardlands.inventory.HardlandsMenu;
import com.hardlands.inventory.MenuInventory;
import com.hardlands.item.InventoryItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public final class PlayerRepeatingTask extends BukkitRunnable {

    private static final long INTERVAL_TICKS = 20L;

    public static void initialize(HardlandsPlugin plugin) {
        new PlayerRepeatingTask().runTaskTimer(plugin, INTERVAL_TICKS, INTERVAL_TICKS);
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(this::tick);
    }

    private void tick(Player player) {
        this.refreshPreparationItem(player);
    }

    private void refreshPreparationItem(Player player) {
        if (player.getOpenInventory().getTopInventory().getHolder() instanceof MenuInventory holder && holder.getMenu() == HardlandsMenu.MAIN) {
            holder.setItem(HardlandsMenu.PREPARATION_SLOT, InventoryItem.PREPARATION.getItem(HardlandsMenu.getPreparationManager()));
        }
    }
}