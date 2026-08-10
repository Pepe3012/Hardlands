package com.hardlands;

import com.hardlands.common.command.CommandInitializer;
import com.hardlands.game.GameController;
import com.hardlands.common.menu.MenuInventoryListener;
import com.hardlands.common.player.PlayerListener;
import com.hardlands.common.player.PlayerRepeatingTask;
import com.hardlands.scenario.ScenarioController;
import com.hardlands.scenario.ScenarioDefinition;
import com.hardlands.world.WorldManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class HardlandsPlugin extends JavaPlugin {

    @Getter private GameController gameController;
    @Getter private ScenarioController scenarioController;
    @Getter private WorldManager worldManager;

    @Override
    public void onEnable() {
        initializeManagers();
        registerCommands();
        registerListeners();

        PlayerRepeatingTask.initialize(this);

        getLogger().info(System.lineSeparator() + """
              _    _          _____  _____  _               _   _ _____   _____
             | |  | |   /\\   |  __ \\|  __ \\| |        /\\   | \\ | |  __ \\ / ____|
             | |__| |  /  \\  | |__) | |  | | |       /  \\  |  \\| | |  | | (___
             |  __  | / /\\ \\ |  _  /| |  | | |      / /\\ \\ | . ` | |  | |\\___ \\
             | |  | |/ ____ \\| | \\ \\| |__| | |____ / ____ \\| |\\  | |__| |____) |
             |_|  |_/_/    \\_\\_|  \\_\\_____/|______/_/    \\_\\_| \\_|_____/|_____/
            """);

        getLogger().info("The plugin has been successfully enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("The plugin has been successfully disabled.");
    }

    private void initializeManagers() {
        gameController = new GameController(this);

        scenarioController = new ScenarioController(this);
        scenarioController.registerScenarios(ScenarioDefinition.values());

        worldManager = new WorldManager();
    }

    private void registerCommands() {
        new CommandInitializer(this).register();
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new MenuInventoryListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
    }
}