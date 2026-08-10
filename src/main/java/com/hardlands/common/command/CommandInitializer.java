package com.hardlands.common.command;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.PaperCommandManager;
import com.hardlands.HardlandsPlugin;
import com.hardlands.scenario.ScenarioType;

import java.util.Collection;
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

        completions.registerStaticCompletion("registered_scenarios", ScenarioType.IDS);
        completions.registerCompletion("active_scenarios", _ -> this.getActiveScenarios());
        completions.registerCompletion("uhc_options", _ -> this.getUhcOptions());
    }

    private Collection<String> getActiveScenarios() {
        return this.plugin.getScenarioController()
                .getActiveScenarioTypes()
                .stream()
                .map(ScenarioType::getId)
                .toList();
    }

    private Collection<String> getUhcOptions() {
        return Stream.concat(
                this.plugin.getGameManager().getOptionHolder().getOptions().keySet().stream(),
                this.plugin.getGameManager().getWorldBorderManager().getOptionHolder().getOptions().keySet().stream()
        ).toList();
    }

    private void registerCommands() {
        this.manager.registerCommand(new HardlandsCommand());
    }
}