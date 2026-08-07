package com.hardlands;

import com.hardlands.command.CommandInitializer;
import com.hardlands.inventory.InventoryListener;
import com.hardlands.player.PlayerListener;
import com.hardlands.player.PlayerRepeatingTask;
import com.hardlands.scenario.ScenarioManager;
import com.hardlands.uhc.UHC;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class HardlandsPlugin extends JavaPlugin {

    @Getter @Setter private static HardlandsPlugin instance;

    @Getter private final ScenarioManager scenarioManager = new ScenarioManager();

    @Getter @Setter private UHC uhc;

    @Override
    public void onEnable() {
        super.getLogger().info("Initializing plugin...");

        setInstance(this);
        this.uhc = new UHC(this);

        this.registerListeners(Bukkit.getPluginManager());
        new CommandInitializer(this).register();

        PlayerRepeatingTask.initialize(this);

        super.getLogger().info(System.lineSeparator() + """
          _    _          _____  _____  _               _   _ _____   _____
         | |  | |   /\\   |  __ \\|  __ \\| |        /\\   | \\ | |  __ \\ / ____|
         | |__| |  /  \\  | |__) | |  | | |       /  \\  |  \\| | |  | | (___
         |  __  | / /\\ \\ |  _  /| |  | | |      / /\\ \\ | . ` | |  | |\\___ \\
         | |  | |/ ____ \\| | \\ \\| |__| | |____ / ____ \\| |\\  | |__| |____) |
         |_|  |_/_/    \\_\\_|  \\_\\_____/|______/_/    \\_\\_| \\_|_____/|_____/
        """);
        super.getLogger().info("The plugin has been successfully enabled.");
    }

    @Override
    public void onDisable() {
        super.getLogger().info("The plugin has been successfully disabled.");
    }

    private void registerListeners(PluginManager pluginManager) {
        pluginManager.registerEvents(new PlayerListener(), this);
        pluginManager.registerEvents(new InventoryListener(), this);
    }
}