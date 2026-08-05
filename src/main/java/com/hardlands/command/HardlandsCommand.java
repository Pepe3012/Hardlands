package com.hardlands.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.hardlands.HardlandsPlugin;
import com.hardlands.option.Option;
import com.hardlands.scenario.Scenario;
import com.hardlands.scenario.ScenarioManager;
import com.hardlands.scenario.ScenarioType;
import com.hardlands.util.ChatMessenger;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

@CommandAlias("hardlands")
@CommandPermission("hardlands.admin")
public final class HardlandsCommand extends BaseCommand {

    @Subcommand("scenarios list")
    private void onScenariosList(Player sender) {
        ScenarioManager manager = HardlandsPlugin.INSTANCE.getScenarioManager();
        ChatMessenger.sendMessage(sender, "<gray>Scenarios:");
        ScenarioType[] types = ScenarioType.values();
        for (int i = 0; i < types.length; i++) {
            ScenarioType type = types[i];
            boolean active = manager.isActive(type);
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray>" + (i + 1) + ". <yellow>" + type.getId() + " <gray>- " + (active ? "<green>active" : "<red>inactive")));
        }
    }

    @Subcommand("scenarios enable")
    @CommandCompletion("@registered_scenarios")
    private void onScenariosEnable(Player sender, @Single String id) {
        ScenarioType type = ScenarioType.fromId(id);
        if (type == null) {
            ChatMessenger.sendMessage(sender, "<red>Scenario '" + id + "' not found.");
            return;
        }
        boolean success = HardlandsPlugin.INSTANCE.getScenarioManager().enable(type);
        ChatMessenger.sendMessage(sender, (success ? "<green>" : "<red>") + "Scenario '" + type.getId() + (success ? "' enabled." : "' is already active."));
    }

    @Subcommand("scenarios disable")
    @CommandCompletion("@active_scenarios")
    private void onScenariosDisable(Player sender, @Single String id) {
        ScenarioType type = ScenarioType.fromId(id);
        if (type == null) {
            ChatMessenger.sendMessage(sender, "<red>Scenario '" + id + "' not found.");
            return;
        }
        boolean success = HardlandsPlugin.INSTANCE.getScenarioManager().disable(type);
        ChatMessenger.sendMessage(sender, (success ? "<green>" : "<red>") + "Scenario '" + type.getId() + (success ? "' disabled." : "' is not active."));
    }

    @Subcommand("scenarios option")
    @CommandCompletion("@active_scenarios")
    private void onScenarioOption(Player sender, @Single String id, @Single String key, @Single String value) {
        ScenarioType type = ScenarioType.fromId(id);
        if (type == null) {
            ChatMessenger.sendMessage(sender, "<red>Scenario '" + id + "' not found.");
            return;
        }
        Scenario scenario = HardlandsPlugin.INSTANCE.getScenarioManager().getActive(type);
        if (scenario == null) {
            ChatMessenger.sendMessage(sender, "<red>Scenario '" + type.getId() + "' is not active.");
            return;
        }
        Option<?> option = scenario.getOptionContainer().get(key);
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

    private static boolean setValue(Option<?> option, String value) {
        try {
            switch (option.getValue()) {
                case Boolean _ -> ((Option<Boolean>) option).setValue(Boolean.parseBoolean(value));
                case Float _ -> ((Option<Float>) option).setValue(Float.parseFloat(value));
                case Integer _ -> ((Option<Integer>) option).setValue(Integer.parseInt(value));
                case String _ -> ((Option<String>) option).setValue(value);
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