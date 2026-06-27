package com.hardlands.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.hardlands.Hardlands;
import com.hardlands.scenario.Scenario;
import com.hardlands.util.ChatMessenger;
import com.hardlands.scenario.ScenarioManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CommandAlias("hardlands")
@CommandPermission("hardlands.admin")
public class HardlandsCommand extends BaseCommand {
    private final ScenarioManager scenarioManager;

    public HardlandsCommand(Hardlands hardlands) {
        this.scenarioManager = hardlands.getScenarioManager();
    }

    @Subcommand("scenarios list")
    private void onScenariosList(Player sender) {
        Map<String, Scenario> registered = this.scenarioManager.getRegisteredScenarios();
        if (registered.isEmpty()) {
            ChatMessenger.sendMessage(sender, "<red>No scenarios registered.");
            return;
        }
        ChatMessenger.sendMessage(sender, "<gray>Scenarios:");
        List<String> ids = new ArrayList<>(registered.keySet());
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

    @Subcommand("scenarios option")
    @CommandCompletion("@registered_scenarios")
    private void onScenarioOption(Player sender, @Single String id, @Single String key, @Single String value) {
        Scenario scenario = this.scenarioManager.getRegisteredScenarios().get(id);
        if (scenario == null) {
            ChatMessenger.sendMessage(sender, "<red>Scenario '" + id + "' not found.");
            return;
        }
        Scenario.Option<?> option = scenario.getOption(key);
        if (option == null) {
            ChatMessenger.sendMessage(sender, "<red>Option '" + key + "' not found.");
            return;
        }
        if (!setValue(option, value)) {
            ChatMessenger.sendMessage(sender, "<red>Invalid value '" + value + "' for " + key + ".");
            return;
        }
        ChatMessenger.sendMessage(sender, "<green>'" + key + "' set to '" + value + "'.");
    }

    @SuppressWarnings("unchecked")
    private static boolean setValue(Scenario.Option<?> option, String value) {
        try {
            switch (option.getValue()) {
                case Boolean _ -> ((Scenario.Option<Boolean>) option).setValue(Boolean.parseBoolean(value));
                case Float _ -> ((Scenario.Option<Float>) option).setValue(Float.parseFloat(value));
                case String _ -> ((Scenario.Option<String>) option).setValue(value);
                default -> {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException _) {
            return false;
        }
    }
}