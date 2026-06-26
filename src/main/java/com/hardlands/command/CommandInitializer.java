package com.hardlands.command;

import co.aikar.commands.PaperCommandManager;
import com.hardlands.Hardlands;

public final class CommandInitializer {
    private CommandInitializer() {}

    public static void initialize(Hardlands hardlands) {
        PaperCommandManager commandManager = new PaperCommandManager(hardlands);

        commandManager.getCommandCompletions().registerAsyncCompletion("registered_scenarios", _ -> hardlands.getScenarioManager().getRegisteredScenarios().keySet());
        commandManager.getCommandCompletions().registerAsyncCompletion("active_scenarios", _ -> hardlands.getScenarioManager().getActiveScenarios().keySet());

        commandManager.registerCommand(new HardlandsCommand(hardlands));
    }
}
