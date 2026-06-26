package com.hardlands.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.hardlands.Hardlands;
import com.hardlands.util.ChatMessenger;
import com.hardlands.scenario.ScenarioManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@CommandAlias("hardlands")
@CommandPermission("hardlands.admin")
public class HardlandsCommand extends BaseCommand {
    private final ScenarioManager scenarioManager;

    public HardlandsCommand(Hardlands plugin) {
        this.scenarioManager = plugin.getScenarioManager();
    }

    @Subcommand("scenarios list")
    private void onScenariosList(Player sender) {
        var registered = this.scenarioManager.getRegisteredScenarios();
        if (registered.isEmpty()) {
            ChatMessenger.sendMessage(sender, "<red>No scenarios registered.");
            return;
        }
        ChatMessenger.sendMessage(sender, "<gray>Scenarios:");
        var ids = new ArrayList<>(registered.keySet());
        for (int i = 0; i < ids.size(); i++) {
            String id = ids.get(i);
            boolean active = this.scenarioManager.getActiveScenarios().containsKey(id);
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray>" + (i + 1) + ". <yellow>" + id + " <gray>- " + (active ? "<green>active" : "<red>inactive")));
        }
    }

    @Subcommand("scenarios enable")
    @CommandCompletion("@registered_scenarios")
    private void onScenariosEnable(Player sender, @Single String id) {
        boolean success = this.scenarioManager.enableScenario(id);
        ChatMessenger.sendMessage(sender, (success ? "<green>" : "<red>") + "Scenario '" + id + (success ? "' enabled." : "' not found or already active."));
    }

    @Subcommand("scenarios disable")
    @CommandCompletion("@active_scenarios")
    private void onScenariosDisable(Player sender, @Single String id) {
        boolean success = this.scenarioManager.disableScenario(id);
        ChatMessenger.sendMessage(sender, (success ? "<green>" : "<red>") + "Scenario '" + id + (success ? "' disabled." : "' not found or not active."));
    }
}