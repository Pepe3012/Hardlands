package io.github.pepe3012.hardlands;

import co.aikar.commands.PaperCommandManager;
import io.github.pepe3012.hardlands.common.command.HardlandsCommand;
import io.github.pepe3012.hardlands.common.player.PlayerListener;
import io.github.pepe3012.hardlands.config.inventory.InventoryDefinition;
import io.github.pepe3012.hardlands.config.inventory.InventoryListener;
import io.github.pepe3012.hardlands.config.inventory.InventoryRegistry;
import io.github.pepe3012.hardlands.game.GameManager;
import io.github.pepe3012.hardlands.scenario.ScenarioDefinition;
import io.github.pepe3012.hardlands.scenario.ScenarioManager;
import io.github.pepe3012.hardlands.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.popcraft.chunky.api.ChunkyAPI;

public final class Hardlands extends JavaPlugin {

    private static Hardlands instance;

    private GameManager gameManager;
    private ScenarioManager scenarioManager;
    private WorldManager worldManager;

    @Override
    public void onEnable() {
        super.getLogger().info("Initializing Hardlands Plugin...");
        setInstance(this);

        this.initializeSystems();

        this.registerListeners(
                new InventoryListener(),
                new PlayerListener()
        );

        new PaperCommandManager(this).registerCommand(new HardlandsCommand());

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

        this.gameManager = new GameManager(this);

        this.worldManager = new WorldManager(requireChunkyApi());

        InventoryRegistry.register(InventoryDefinition.values());
    }

    private void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, this);
        }
    }

    public static NamespacedKey namespacedKey(String key) {
        return new NamespacedKey(instance, key.toUpperCase());
    }

    private static ChunkyAPI requireChunkyApi() {
        ChunkyAPI chunkyApi = Bukkit.getServicesManager().load(ChunkyAPI.class);

        if (chunkyApi == null) {
            throw new IllegalStateException("Chunky API is unavailable. Ensure Chunky plugin is installed and enabled.");
        }

        return chunkyApi;
    }

    public GameManager getGameController() {
        return this.gameManager;
    }

    public ScenarioManager getScenarioManager() {
        return this.scenarioManager;
    }

    public WorldManager getWorldManager() {
        return this.worldManager;
    }

    public static Hardlands getInstance() {
        return instance;
    }

    private static void setInstance(Hardlands instance) {
        Hardlands.instance = instance;
    }
}