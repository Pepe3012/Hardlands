package org.heather.hardlands;

import org.heather.hardlands.common.command.CommandInitializer;
import org.heather.hardlands.common.player.PlayerListener;
import org.heather.hardlands.game.GameController;
import org.heather.hardlands.common.player.PlayerTickTask;
import org.heather.hardlands.inventory.InventoryScreenType;
import org.heather.hardlands.inventory.InventoryListener;
import org.heather.hardlands.inventory.InventoryManager;
import org.heather.hardlands.scenario.ScenarioManager;
import org.heather.hardlands.scenario.ScenarioDefinition;
import org.heather.hardlands.world.WorldManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.popcraft.chunky.api.ChunkyAPI;

public final class Hardlands extends JavaPlugin {

    @Getter private GameController gameController;
    @Getter private InventoryManager inventoryManager;
    @Getter private ScenarioManager scenarioManager;
    @Getter private WorldManager worldManager;

    @Override
    public void onEnable() {
        super.getLogger().info("Initializing Hardlands Plugin...");

        this.initializeSystems();
        this.registerListeners();

        CommandInitializer.initialize(this);

        super.getLogger().info("Initializing repeating tasks...");
        this.initializeRepeatingTasks();

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
        this.gameController = new GameController(this);

        this.inventoryManager = new InventoryManager(this);
        this.inventoryManager.registerInventories(InventoryScreenType.values());

        this.scenarioManager = new ScenarioManager(this);
        this.scenarioManager.registerScenarios(ScenarioDefinition.values());

        this.worldManager = new WorldManager(requireChunkyApi());
    }

    private void initializeRepeatingTasks() {
        PlayerTickTask.initialize(this, 20L);
    }

    private void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new InventoryListener(), this);
        pluginManager.registerEvents(new PlayerListener(), this);
    }

    private static ChunkyAPI requireChunkyApi() {
        ChunkyAPI chunkyApi = Bukkit.getServicesManager().load(ChunkyAPI.class);

        if (chunkyApi == null) {
            throw new IllegalStateException("Chunky API is unavailable");
        }

        return chunkyApi;
    }
}