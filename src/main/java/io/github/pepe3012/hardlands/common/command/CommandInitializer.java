package io.github.pepe3012.hardlands.common.command;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.PaperCommandManager;
import io.github.pepe3012.hardlands.Hardlands;
import io.github.pepe3012.hardlands.common.item.HardlandsItems;
import io.github.pepe3012.hardlands.scenario.ScenarioDefinition;

import java.util.List;

public final class CommandInitializer {

    private CommandInitializer() {}

    public static void initialize(Hardlands plugin) {
        PaperCommandManager manager = new PaperCommandManager(plugin);

        registerCompletions(plugin, manager);
        registerCommands(plugin, manager);
    }

    private static void registerCompletions(Hardlands plugin, PaperCommandManager manager) {
        CommandCompletions<BukkitCommandCompletionContext> completions = manager.getCommandCompletions();
        registerStaticCompletions(completions);
        registerDynamicCompletions(plugin, completions);
    }

    private static void registerStaticCompletions(CommandCompletions<BukkitCommandCompletionContext> completions) {
        completions.registerStaticCompletion("registered_scenarios", ScenarioDefinition.IDENTIFIERS);
        completions.registerStaticCompletion("hardlands_items", HardlandsItems.IDENTIFIERS);
    }

    private static void registerDynamicCompletions(Hardlands plugin, CommandCompletions<BukkitCommandCompletionContext> completions) {
        completions.registerCompletion("active_scenarios", _ -> plugin.getScenarioManager()
                .getActiveScenarioDefinitions()
                .stream()
                .map(ScenarioDefinition::getIdentifier)
                .toList());

        completions.registerCompletion("scenario_options", context -> {
            String identifier = context.getContextValue(String.class, 1);
            ScenarioDefinition definition = ScenarioDefinition.findByIdentifier(identifier).orElse(null);

            if (definition == null || !plugin.getScenarioManager().isScenarioRegistered(definition)) {
                return List.of();
            }

            return plugin.getScenarioManager()
                    .getRegisteredScenario(definition)
                    .getRegisteredOptions()
                    .keySet();
        });
    }

    private static void registerCommands(Hardlands plugin, PaperCommandManager manager) {
        manager.registerCommand(new HardlandsCommand(plugin));
    }
}