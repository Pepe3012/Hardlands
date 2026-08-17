package io.github.pepe3012.hardlands;

import co.aikar.commands.PaperCommandManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.pepe3012.hardlands.common.command.HardlandsCommand;
import io.github.pepe3012.hardlands.core.ThreadScheduler;
import io.github.pepe3012.hardlands.listener.PlayerListener;
import io.github.pepe3012.hardlands.module.GeneralConfiguration;
import io.github.pepe3012.hardlands.module.PresetRepository;
import io.github.pepe3012.hardlands.module.inventory.InventoryDefinition;
import io.github.pepe3012.hardlands.listener.InventoryListener;
import io.github.pepe3012.hardlands.module.inventory.InventoryRegistry;
import io.github.pepe3012.hardlands.module.scenario.ScenarioManager;
import io.github.pepe3012.hardlands.module.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.popcraft.chunky.api.ChunkyAPI;

import java.util.concurrent.ThreadLocalRandom;

public final class Hardlands extends JavaPlugin {

    public static final ThreadLocalRandom RANDOMIZER = ThreadLocalRandom.current();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Hardlands instance;

    private PresetRepository presetRepository;
    private ThreadScheduler threadScheduler;

    private GeneralConfiguration generalConfigurations;
    private ScenarioManager scenarioManager;
    private WorldManager worldManager;

    @Override
    public void onEnable() {
        getLogger().info("Initializing...");
        setInstance(this);

        presetRepository = PresetRepository.create(this);
        threadScheduler = new ThreadScheduler(this);

        initializeModules();
        initializeListeners();
        initializeCommands();

        getLogger().info(System.lineSeparator() + """
             _    _          _____  _____  _               _   _ _____   _____
            | |  | |   /\\   |  __ \\|  __ \\| |        /\\   | \\ | |  __ \\ / ____|
            | |__| |  /  \\  | |__) | |  | | |       /  \\  |  \\| | |  | | (___
            |  __  | / /\\ \\ |  _  /| |  | | |      / /\\ \\ | . ` | |  | |\\___ \\
            | |  | |/ ____ \\| | \\ \\| |__| | |____ / ____ \\| |\\  | |__| |____) |
            |_|  |_/_/    \\_\\_|  \\_\\_____/|______/_/    \\_\\_| \\_|_____/|_____/
            """);
        getLogger().info("Initialized.");
    }

    private void initializeModules() {
        this.generalConfigurations = new GeneralConfiguration();
        this.scenarioManager = new ScenarioManager(this);
        this.worldManager = new WorldManager(requireChunkyApi());

        InventoryRegistry.register(InventoryDefinition.values());
    }

    private void initializeListeners() {
        var pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents( new InventoryListener(), this);
        pluginManager.registerEvents( new PlayerListener(), this);
    }

    private void initializeCommands() {
        var paperCommandManager = new PaperCommandManager(this);
        paperCommandManager.registerCommand(new HardlandsCommand());
    }

    @Override
    public void onDisable() {
        super.getLogger().info("Terminating...");

        super.getLogger().info("Terminated.");
    }


    public static Hardlands getInstance() {
        return instance;
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