package com.hardlands;

import com.hardlands.listener.PlayerListener;
import com.hardlands.scenario.ScenarioManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Hardlands extends JavaPlugin {
    private static final ScenarioManager SCENARIO_MANAGER = new ScenarioManager();
    private static Hardlands instance;

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(PlayerListener.INSTANCE, this);
        super.getLogger().info("The plugin has been enabled successfully.");
    }

    @Override
    public void onDisable() {
        super.getLogger().info("The plugin has been disabled successfully.");
    }

    public static ScenarioManager getScenarioManager() {
        return SCENARIO_MANAGER;
    }

    public static Hardlands getInstance() {
        return instance;
    }
}