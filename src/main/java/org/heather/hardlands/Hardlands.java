package org.heather.hardlands;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.heather.hardlands.common.command.CommandInitializer;
import org.heather.hardlands.common.player.PlayerListener;
import org.heather.hardlands.inventory.*;
import org.heather.hardlands.task.PlayerUpdateTask;
import org.heather.hardlands.game.GameController;
import org.heather.hardlands.scenario.ScenarioDefinition;
import org.heather.hardlands.scenario.ScenarioManager;
import org.heather.hardlands.world.WorldManager;
import org.popcraft.chunky.api.ChunkyAPI;

public final class Hardlands extends JavaPlugin {

    @Getter @Setter private static Hardlands instance;

    @Getter private GameController gameController;
    @Getter private ScenarioManager scenarioManager;
    @Getter private WorldManager worldManager;

    @Override
    public void onEnable() {
        super.getLogger().info("Initializing Hardlands Plugin...");
        setInstance(this);

        this.initializeSystems();
        this.registerListeners();
        this.initializeRepeatingTasks();
        CommandInitializer.initialize(this);

        super.getLogger().info(System.lineSeparator() + """
                  _    _          _____  _____  _               _   _ _____   _____
                 | |  | |   /\\   |  __ \\|  __ \\| |        /\\   | \\ | |  __ \\ / ____|
                 | |__| |  /  \\  | |__) | |  | | |       /  \\  |  \\| | |  | | (___
                 |  __  | / /\\ \\ |  _  /| |  | | |      / /\\ \\ | . ` | |  | |\\___ \\
                 | |  | |/ ____ \\| | \\ \\| |__| | |____ / ____ \\| |\\  | |__| |____) |
                 |_|  |_/_/    \\_\\_|  \\_\\_____/|______/_/    \\_\\_| \\_|_____/|_____/
                """);
        super.getLogger().info("Hardlands has been enabled.");
    }

    @Override
    public void onDisable() {
        super.getLogger().info("Hardlands has been disabled.");
    }

    private void initializeSystems() {
        this.scenarioManager = new ScenarioManager(this);
        this.scenarioManager.registerScenarios(ScenarioDefinition.values());

        this.gameController = new GameController(this);

        this.worldManager = new WorldManager(requireChunkyApi());

        InventoryRegistry.register(InventoryDefinition.values());
        InventoryRegistry.freeze();
    }

    private void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new InventoryListener(), this);
        pluginManager.registerEvents(new PlayerListener(), this);
    }

    private void initializeRepeatingTasks() {
        super.getLogger().info("Initializing repeating tasks...");
        PlayerUpdateTask.initialize(this, 20L);
    }

    public NamespacedKey namespacedKey(String key) {
        return new NamespacedKey(this, key.toUpperCase());
    }

    private static ChunkyAPI requireChunkyApi() {
        ChunkyAPI chunkyApi = Bukkit.getServicesManager().load(ChunkyAPI.class);

        if (chunkyApi == null) {
            throw new IllegalStateException("Chunky API is unavailable. Ensure Chunky plugin is installed and enabled.");
        }

        return chunkyApi;
    }
}