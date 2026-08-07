package com.hardlands;

import com.hardlands.command.CommandInitializer;
import com.hardlands.menu.MenuInventoryListener;
import com.hardlands.player.PlayerListener;
import com.hardlands.player.PlayerRepeatingTask;
import com.hardlands.scenario.ScenarioManager;
import com.hardlands.uhc.UHC;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class HardlandsPlugin extends JavaPlugin {

    private static final String BANNER = """
              _    _          _____  _____  _               _   _ _____   _____
             | |  | |   /\\   |  __ \\|  __ \\| |        /\\   | \\ | |  __ \\ / ____|
             | |__| |  /  \\  | |__) | |  | | |       /  \\  |  \\| | |  | | (___
             |  __  | / /\\ \\ |  _  /| |  | | |      / /\\ \\ | . ` | |  | |\\___ \\
             | |  | |/ ____ \\| | \\ \\| |__| | |____ / ____ \\| |\\  | |__| |____) |
             |_|  |_/_/    \\_\\_|  \\_\\_____/|______/_/    \\_\\_| \\_|_____/|_____/
            """;

    @Getter @Setter private static HardlandsPlugin instance;

    @Getter private final ScenarioManager scenarioManager = new ScenarioManager();
    @Getter private UHC uhc;

    @Override
    public void onEnable() {
        setInstance(this);

        this.getLogger().info("Initializing UHC...");
        this.uhc = new UHC(this);

        this.getLogger().info("Registering listeners...");
        this.registerListeners();

        this.getLogger().info("Registering commands...");
        new CommandInitializer(this).register();

        this.getLogger().info("Initializing repeating tasks...");
        PlayerRepeatingTask.initialize(this);

        this.getLogger().info(System.lineSeparator() + BANNER);
        this.getLogger().info("The plugin has been successfully enabled.");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("The plugin has been successfully disabled.");
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new MenuInventoryListener(), this);
    }
}