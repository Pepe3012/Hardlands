package org.heather.hardlands.common.player;

import org.heather.hardlands.Hardlands;
import org.heather.hardlands.common.menu.MenuInventory;
import org.heather.hardlands.common.menu.screen.MainMenu;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public final class PlayerTickTask {

    private PlayerTickTask() {}

    public static void initialize(final Hardlands plugin, long period) {
        Server server = Bukkit.getServer();
        server.getScheduler().runTaskTimer(plugin, () -> server.getOnlinePlayers().forEach(PlayerTickTask::tick), 0L, period);
    }

    private static void tick(Player player) {
        refreshMenu(player);
    }

    private static void refreshMenu(Player player) {
        if (player.getOpenInventory().getTopInventory().getHolder() instanceof MenuInventory menu && menu.getMenu() == MainMenu.INSTANCE) {
            MainMenu.refreshPreparation(menu);
        }
    }
}