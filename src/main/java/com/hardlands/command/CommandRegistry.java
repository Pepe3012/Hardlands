package com.hardlands.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.PaperCommandManager;
import com.hardlands.Hardlands;
import com.hardlands.scenario.ScenarioManager;

import java.util.List;
import java.util.function.Function;

public final class CommandRegistry {
    private final PaperCommandManager manager;
    private final Hardlands plugin;

    public CommandRegistry(Hardlands plugin) {
        this.manager = new PaperCommandManager(plugin);
        this.plugin = plugin;
    }

    public void initializeCompletions() {
        CommandCompletions<BukkitCommandCompletionContext> completions = this.manager.getCommandCompletions();

        ScenarioManager scenarioManager = this.plugin.getScenarioManager();
        completions.registerAsyncCompletion("registered_scenarios", _ -> scenarioManager.getRegisteredScenarios().keySet());
        completions.registerAsyncCompletion("active_scenarios", _ -> scenarioManager.getActiveScenarios().keySet());
    }

    public void registerCommands(List<Function<Hardlands, BaseCommand>> factories) {
        factories.forEach(factory -> this.manager.registerCommand(factory.apply(this.plugin)));
    }
}