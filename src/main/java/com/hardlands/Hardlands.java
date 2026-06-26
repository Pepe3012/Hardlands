package com.hardlands;

import com.hardlands.command.CommandRegistry;
import com.hardlands.command.HardlandsCommand;
import com.hardlands.listener.PlayerListener;
import com.hardlands.scenario.ScenarioManager;
import com.hardlands.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class Hardlands extends JavaPlugin {
    private ScenarioManager scenarioManager;

    @Override
    public void onEnable() {
        this.scenarioManager = new ScenarioManager(this);

        Scenarios.initialize(this.scenarioManager);

        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

        CommandRegistry commandRegistry = new CommandRegistry(this);
        commandRegistry.initializeCompletions();
        commandRegistry.registerCommands(List.of(
                HardlandsCommand::new
        ));

        super.getLogger().info("The plugin has been successfully enabled.");
    }

    @Override
    public void onDisable() {
        super.getLogger().info("The plugin has been successfully disabled.");
    }

    public ScenarioManager getScenarioManager() {
        return this.scenarioManager;
    }
}