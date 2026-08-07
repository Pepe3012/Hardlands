package com.hardlands.command;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.PaperCommandManager;
import com.hardlands.HardlandsPlugin;
import com.hardlands.scenario.ScenarioType;

import java.util.stream.Stream;

public final class CommandInitializer {

    private final HardlandsPlugin plugin;
    private final PaperCommandManager manager;

    public CommandInitializer(HardlandsPlugin plugin) {
        this.plugin = plugin;
        this.manager = new PaperCommandManager(plugin);
    }

    public void register() {
        this.registerCompletions();
        this.registerCommands();
    }

    private void registerCompletions() {
        CommandCompletions<BukkitCommandCompletionContext> completions = this.manager.getCommandCompletions();

        completions.registerAsyncCompletion("registered_scenarios", context -> ScenarioType.IDS);

        completions.registerAsyncCompletion("active_scenarios", context -> this.plugin.getScenarioManager()
                .getActiveScenarioTypes()
                .stream()
                .map(ScenarioType::getId)
                .toList());

        completions.registerAsyncCompletion("uhc_options", context -> Stream.concat(
                this.plugin.getUhc().getOptionContainer().getOptions().keySet().stream(),
                this.plugin.getUhc().getWorldBorderManager().getOptionContainer().getOptions().keySet().stream()
        ).toList());
    }

    private void registerCommands() {
        this.manager.registerCommand(new HardlandsCommand());
    }
}