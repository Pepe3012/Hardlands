package com.hardlands;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.PaperCommandManager;
import com.hardlands.command.HardlandsCommand;
import com.hardlands.listener.PlayerListener;
import com.hardlands.scenario.ScenarioManager;
import com.hardlands.scenario.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Hardlands extends JavaPlugin {
    private final ScenarioManager scenarioManager = new ScenarioManager();

    @Override
    public void onEnable() {
        Scenarios.initialize(this.scenarioManager);

        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

        PaperCommandManager paperCommandManager = new PaperCommandManager(this);
        this.registerCommandCompletions(paperCommandManager);
        paperCommandManager.registerCommand(new HardlandsCommand());

        super.getLogger().info("The plugin has been successfully enabled.");
    }

    @Override
    public void onDisable() {
        super.getLogger().info("The plugin has been successfully disabled.");
    }

    private void registerCommandCompletions(PaperCommandManager manager) {
        CommandCompletions<BukkitCommandCompletionContext> completions = manager.getCommandCompletions();

        completions.registerAsyncCompletion("registered_scenarios", _ -> this.scenarioManager.getRegisteredScenarios().keySet());
        completions.registerAsyncCompletion("active_scenarios", _ -> this.scenarioManager.getActiveScenarios().keySet());
    }

    public ScenarioManager getScenarioManager() {
        return this.scenarioManager;
    }

    public static Hardlands getInstance() {
        return JavaPlugin.getPlugin(Hardlands.class);
    }
}