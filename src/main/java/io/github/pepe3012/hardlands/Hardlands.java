package io.github.pepe3012.hardlands;

import co.aikar.commands.PaperCommandManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.pepe3012.hardlands.common.command.HardlandsCommand;
import io.github.pepe3012.hardlands.common.player.PlayerListener;
import io.github.pepe3012.hardlands.data.HardlandsData;
import io.github.pepe3012.hardlands.inventory.InventoryDefinition;
import io.github.pepe3012.hardlands.inventory.InventoryListener;
import io.github.pepe3012.hardlands.inventory.InventoryRegistry;
import io.github.pepe3012.hardlands.scenario.ScenarioDefinition;
import io.github.pepe3012.hardlands.scenario.ScenarioManager;
import io.github.pepe3012.hardlands.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.popcraft.chunky.api.ChunkyAPI;

public final class Hardlands extends JavaPlugin {

    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static Hardlands instance;

    private ScenarioManager scenarioManager;
    private WorldManager worldManager;

    @Override
    public void onEnable() {
        super.getLogger().info("Initializing...");
        setInstance(this);

        this.initializeSystems();
        this.initializeListeners();
        this.initializeCommands();

        HardlandsData.load(this);

        super.getLogger().info(System.lineSeparator() + """
      _    _          _____  _____  _               _   _ _____   _____
     | |  | |   /\\   |  __ \\|  __ \\| |        /\\   | \\ | |  __ \\ / ____|
     | |__| |  /  \\  | |__) | |  | | |       /  \\  |  \\| | |  | | (___
     |  __  | / /\\ \\ |  _  /| |  | | |      / /\\ \\ | . ` | |  | |\\___ \\
     | |  | |/ ____ \\| | \\ \\| |__| | |____ / ____ \\| |\\  | |__| |____) |
     |_|  |_/_/    \\_\\_|  \\_\\_____/|______/_/    \\_\\_| \\_|_____/|_____/
    """);

        super.getLogger().info("Initialized.");
    }

    @Override
    public void onDisable() {
        super.getLogger().info("Terminating...");

        HardlandsData.save(this);

        super.getLogger().info("Terminated.");
    }

    private void initializeSystems() {
        this.scenarioManager = new ScenarioManager(this);
        this.worldManager = new WorldManager(requireChunkyApi());

        InventoryRegistry.register(InventoryDefinition.values());
    }

    private void initializeListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents( new InventoryListener(), this);
        pluginManager.registerEvents( new PlayerListener(), this);
    }

    private void initializeCommands() {
        PaperCommandManager paperCommandManager = new PaperCommandManager(this);
        paperCommandManager.registerCommand(new HardlandsCommand());
    }

    public ScenarioManager getScenarioManager() {
        return this.scenarioManager;
    }

    public WorldManager getWorldManager() {
        return this.worldManager;
    }

    public static NamespacedKey namespacedKey(String key) {
        return new NamespacedKey(instance, key.toUpperCase());
    }

    public static Hardlands getInstance() {
        return instance;
    }

    private static void setInstance(Hardlands instance) {
        Hardlands.instance = instance;
    }

    private static ChunkyAPI requireChunkyApi() {
        ChunkyAPI chunkyApi = Bukkit.getServicesManager().load(ChunkyAPI.class);

        if (chunkyApi == null) {
            throw new IllegalStateException("Chunky API is unavailable. Ensure Chunky plugin is installed and enabled.");
        }

        return chunkyApi;
    }
}