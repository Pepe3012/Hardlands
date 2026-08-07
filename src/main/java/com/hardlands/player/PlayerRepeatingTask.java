package com.hardlands.player;

import com.hardlands.HardlandsPlugin;
import com.hardlands.menu.screen.HardlandsMenu;
import com.hardlands.menu.MenuInventory;
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
        if (player.getOpenInventory().getTopInventory().getHolder() instanceof MenuInventory menu
                && menu.getMenu() == HardlandsMenu.MAIN) {
            HardlandsMenu.refreshPreparationItem(menu);
        }
    }
}