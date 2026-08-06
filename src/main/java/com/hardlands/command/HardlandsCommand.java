package com.hardlands.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.hardlands.HardlandsPlugin;
import com.hardlands.inventory.HardlandsMenu;
import com.hardlands.util.option.Option;
import com.hardlands.scenario.Scenario;
import com.hardlands.scenario.ScenarioManager;
import com.hardlands.scenario.ScenarioType;
import com.hardlands.uhc.PreparationManager;
import com.hardlands.uhc.UHC;
import com.hardlands.util.ChatMessenger;
import com.hardlands.util.TickConverter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

@CommandAlias("hardlands|hl")
@CommandPermission("hardlands.admin")
public final class HardlandsCommand extends BaseCommand {

    @Default
    private void onUhc(Player player) {
        HardlandsMenu.MAIN.open(player);
    }
    
    @Subcommand("scenarios list")
    private void onScenariosList(CommandSender sender) {
        ScenarioManager manager = HardlandsPlugin.getInstance().getScenarioManager();
        ScenarioType[] types = ScenarioType.values();

        ChatMessenger.sendMessage(sender, "<gray>Scenarios:");

        for (int i = 0; i < types.length; i++) {
            ScenarioType type = types[i];
            String state = manager.isActive(type) ? "<green>active" : "<red>inactive";
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray>" + (i + 1) + ". <yellow>" + type.getId() + " <gray>- " + state));
        }
    }

    @Subcommand("scenarios enable")
    @CommandCompletion("@registered_scenarios")
    private void onScenariosEnable(CommandSender sender, @Single String id) {
        if (!canModifyScenarios(sender)) return;

        ScenarioType type = ScenarioType.fromId(id);

        if (type == null) {
            ChatMessenger.sendMessage(sender, "<red>Scenario '" + id + "' not found.");
            return;
        }

        boolean enabled = HardlandsPlugin.getInstance().getScenarioManager().enable(type);
        ChatMessenger.sendMessage(sender, enabled ? "<green>Scenario '" + type.getId() + "' enabled." : "<red>Scenario '" + type.getId() + "' is already active.");
    }

    @Subcommand("scenarios disable")
    @CommandCompletion("@active_scenarios")
    private void onScenariosDisable(CommandSender sender, @Single String id) {
        if (!canModifyScenarios(sender)) return;

        ScenarioType type = ScenarioType.fromId(id);

        if (type == null) {
            ChatMessenger.sendMessage(sender, "<red>Scenario '" + id + "' not found.");
            return;
        }

        boolean disabled = HardlandsPlugin.getInstance().getScenarioManager().disable(type);
        ChatMessenger.sendMessage(sender, disabled ? "<green>Scenario '" + type.getId() + "' disabled." : "<red>Scenario '" + type.getId() + "' is not active.");
    }

    @Subcommand("scenarios option")
    @CommandCompletion("@active_scenarios")
    private void onScenarioOption(CommandSender sender, @Single String id, @Single String key, @Single String value) {
        if (!canModifyScenarios(sender)) return;

        ScenarioType type = ScenarioType.fromId(id);

        if (type == null) {
            ChatMessenger.sendMessage(sender, "<red>Scenario '" + id + "' not found.");
            return;
        }

        Scenario scenario = HardlandsPlugin.getInstance().getScenarioManager().getActive(type);

        if (scenario == null) {
            ChatMessenger.sendMessage(sender, "<red>Scenario '" + type.getId() + "' is not active.");
            return;
        }

        Option<?> option = scenario.getOptionContainer().get(key);

        if (option == null) {
            ChatMessenger.sendMessage(sender, "<red>Option '" + key + "' not found.");
            return;
        }

        if (!setSimpleValue(option, value)) {
            ChatMessenger.sendMessage(sender, "<red>Invalid value '" + value + "' for '" + key + "'.");
            return;
        }

        ChatMessenger.sendMessage(sender, "<green>Option '" + key + "' set to <yellow>" + value + "<green>.");
    }

    @Subcommand("uhc")
    private void onUhcHelp(CommandSender sender) {
        ChatMessenger.sendMessage(sender, "<gray>UHC commands:");
        sendLine(sender, "/hardlands uhc create", "Create a new UHC session");
        sendLine(sender, "/hardlands uhc options list", "View the configuration");
        sendLine(sender, "/hardlands uhc options set <key> <value>", "Change an option");
        sendLine(sender, "/hardlands uhc prepare", "Prepare and pregenerate the world");
        sendLine(sender, "/hardlands uhc prepare cancel", "Cancel world preparation");
        sendLine(sender, "/hardlands uhc start", "Start the prepared game");
        sendLine(sender, "/hardlands uhc next", "Advance the current phase");
        sendLine(sender, "/hardlands uhc status", "View the current state");
        sendLine(sender, "/hardlands uhc stop", "Stop the current game");
        sendLine(sender, "/hardlands uhc reset", "Reset the session");
        sendLine(sender, "/hardlands uhc delete", "Delete the session");
    }

    @Subcommand("uhc create")
    private void onUhcCreate(CommandSender sender) {
        if (HardlandsPlugin.getInstance().getUhc() != null) {
            ChatMessenger.sendMessage(sender, "<red>A UHC session already exists. Reset or delete it first.");
            return;
        }

        HardlandsPlugin.getInstance().setUhc(new UHC(HardlandsPlugin.getInstance()));
        ChatMessenger.sendMessage(sender, "<green>UHC session created. Configure it and run <yellow>/hardlands uhc prepare<green>.");
    }

    @Subcommand("uhc delete")
    private void onUhcDelete(CommandSender sender) {
        UHC uhc = requireUhc(sender);
        if (uhc == null) return;

        if (uhc.isRunning()) {
            ChatMessenger.sendMessage(sender, "<red>Stop the UHC before deleting it.");
            return;
        }

        if (uhc.getPreparationManager().getState() == PreparationManager.PreparationState.IN_PROGRESS) {
            ChatMessenger.sendMessage(sender, "<red>Cancel world preparation before deleting the UHC.");
            return;
        }

        HardlandsPlugin.getInstance().setUhc(null);
        ChatMessenger.sendMessage(sender, "<green>UHC session deleted.");
    }

    @Subcommand("uhc prepare")
    private void onUhcPrepare(CommandSender sender) {
        UHC uhc = requireUhc(sender);
        if (uhc == null) return;

        if (uhc.isRunning()) {
            ChatMessenger.sendMessage(sender, "<red>The UHC is already running.");
            return;
        }

        try {
            uhc.getPreparationManager().startPreparation();
            ChatMessenger.sendMessage(sender, "<green>World pregeneration started with Chunky.");
        } catch (IllegalStateException exception) {
            sendFailure(sender, exception);
        }
    }

    @Subcommand("uhc prepare cancel")
    private void onUhcPrepareCancel(CommandSender sender) {
        UHC uhc = requireUhc(sender);
        if (uhc == null) return;

        try {
            uhc.getPreparationManager().cancelPreparation();
            ChatMessenger.sendMessage(sender, "<green>World pregeneration cancelled.");
        } catch (IllegalStateException exception) {
            sendFailure(sender, exception);
        }
    }

    @Subcommand("uhc start")
    private void onUhcStart(CommandSender sender) {
        UHC uhc = requireUhc(sender);
        if (uhc == null) return;

        try {
            uhc.start();
            ChatMessenger.sendMessage(sender, "<green>UHC started in the <yellow>" + uhc.getPhase().getDisplayName() + "<green> phase.");
        } catch (IllegalStateException exception) {
            sendFailure(sender, exception);
        }
    }

    @Subcommand("uhc stop")
    private void onUhcStop(CommandSender sender) {
        UHC uhc = requireUhc(sender);
        if (uhc == null) return;

        try {
            uhc.stop();
            ChatMessenger.sendMessage(sender, "<green>UHC stopped.");
        } catch (IllegalStateException exception) {
            sendFailure(sender, exception);
        }
    }

    @Subcommand("uhc reset")
    private void onUhcReset(CommandSender sender) {
        UHC uhc = requireUhc(sender);
        if (uhc == null) return;

        try {
            uhc.reset();
            ChatMessenger.sendMessage(sender, "<green>UHC reset to the lobby. World preparation must be run again.");
        } catch (IllegalStateException exception) {
            sendFailure(sender, exception);
        }
    }

    @Subcommand("uhc next")
    private void onUhcNext(CommandSender sender) {
        UHC uhc = requireUhc(sender);
        if (uhc == null) return;

        try {
            uhc.advancePhase();
            ChatMessenger.sendMessage(sender, "<green>Advanced to <yellow>" + uhc.getPhase().getDisplayName() + "<green>.");
        } catch (IllegalStateException exception) {
            sendFailure(sender, exception);
        }
    }

    @Subcommand("uhc status")
    private void onUhcStatus(CommandSender sender) {
        UHC uhc = requireUhc(sender);
        if (uhc == null) return;

        PreparationManager preparation = uhc.getPreparationManager();

        ChatMessenger.sendMessage(sender, "<gray>UHC status:");
        sendLine(sender, "Phase", uhc.getPhase().getDisplayName());
        sendLine(sender, "Running", uhc.isRunning() ? "<green>yes" : "<red>no");
        sendLine(sender, "PvP", uhc.isPvpEnabled() ? "<green>enabled" : "<red>disabled");
        sendLine(sender, "Preparation", preparation.getState().getDisplayName());
        sendLine(sender, "Configuration", uhc.isConfigurationValid() ? "<green>valid" : "<red>invalid");
        sendLine(sender, "Active scenarios", String.valueOf(HardlandsPlugin.getInstance().getScenarioManager().getActive().size()));

        if (preparation.getActiveWorldName() != null) sendLine(sender, "Preparing world", preparation.getActiveWorldName());

        long remainingTicks = uhc.getRemainingPhaseTicks();

        if (remainingTicks >= 0L) sendLine(sender, "Phase remaining", formatTicks(remainingTicks));
    }

    @Subcommand("uhc options list")
    private void onUhcOptionsList(CommandSender sender) {
        UHC uhc = requireUhc(sender);
        if (uhc == null) return;

        ChatMessenger.sendMessage(sender, "<gray>UHC options:");
        uhc.getOptionContainer().getOptions().forEach((key, option) -> sendLine(sender, key, formatOptionValue(key, option.getValue())));

        ChatMessenger.sendMessage(sender, "<gray>World border options:");
        uhc.getWorldBorderManager().getOptionContainer().getOptions().forEach((key, option) -> sendLine(sender, key, formatOptionValue(key, option.getValue())));
    }

    @Subcommand("uhc options set")
    @CommandCompletion("@uhc_options")
    private void onUhcOptionSet(CommandSender sender, @Single String key, @Single String value) {
        UHC uhc = requireUhc(sender);
        if (uhc == null) return;

        if (uhc.isRunning()) {
            ChatMessenger.sendMessage(sender, "<red>UHC options cannot be changed while the game is running.");
            return;
        }

        Option<?> option = uhc.getOptionContainer().get(key);
        boolean borderOption = false;

        if (option == null) {
            option = uhc.getWorldBorderManager().getOptionContainer().get(key);
            borderOption = option != null;
        }

        if (option == null) {
            ChatMessenger.sendMessage(sender, "<red>UHC option '" + key + "' not found.");
            return;
        }

        if (borderOption && uhc.getPreparationManager().isStarted()) {
            ChatMessenger.sendMessage(sender, "<red>World border options are locked after preparation starts. Reset the UHC first.");
            return;
        }

        Object previousValue = option.getValue();

        if (!setUhcValue(option, key, value) || !option.isValid() || !uhc.isConfigurationValid()) {
            restoreValue(option, previousValue);
            ChatMessenger.sendMessage(sender, "<red>Invalid value '" + value + "' for '" + key + "'.");
            return;
        }

        ChatMessenger.sendMessage(sender, "<green>Option '" + key + "' set to <yellow>" + formatOptionValue(key, option.getValue()) + "<green>.");
    }

    private static boolean canModifyScenarios(CommandSender sender) {
        UHC uhc = HardlandsPlugin.getInstance().getUhc();
        if (uhc == null || !uhc.isRunning()) return true;

        ChatMessenger.sendMessage(sender, "<red>Scenarios cannot be changed while the UHC is running.");
        return false;
    }

    private static UHC requireUhc(CommandSender sender) {
        UHC uhc = HardlandsPlugin.getInstance().getUhc();

        if (uhc == null) ChatMessenger.sendMessage(sender, "<red>No UHC session exists. Run <yellow>/hardlands uhc create<red> first.");

        return uhc;
    }

    private static boolean setSimpleValue(Option<?> option, String value) {
        Object previousValue = option.getValue();

        try {
            switch (previousValue) {
                case Boolean _ -> ((Option<Boolean>) option).setValue(parseBoolean(value));
                case Float _ -> ((Option<Float>) option).setValue(Float.parseFloat(value));
                case Double _ -> ((Option<Double>) option).setValue(Double.parseDouble(value));
                case Integer _ -> ((Option<Integer>) option).setValue(Integer.parseInt(value));
                case String _ -> ((Option<String>) option).setValue(value);
                case null, default -> {
                    return false;
                }
            }

            if (option.isValid()) return true;
        } catch (IllegalArgumentException _) {}

        restoreValue(option, previousValue);
        return false;
    }

    private static boolean setUhcValue(Option<?> option, String key, String value) {
        try {
            Object currentValue = option.getValue();

            if (currentValue instanceof UHC.MeetupDuration) {
                UHC.MeetupDuration duration = value.equalsIgnoreCase("infinite") ? UHC.MeetupDuration.INFINITE : new UHC.MeetupDuration(parseDurationTicks(value));
                ((Option<UHC.MeetupDuration>) option).setValue(duration);
                return true;
            }

            if (currentValue instanceof World) {
                World world = Bukkit.getWorld(value);
                if (world == null) return false;

                ((Option<World>) option).setValue(world);
                return true;
            }

            if (currentValue instanceof Integer) {
                ((Option<Integer>) option).setValue(isDurationOption(key) ? parseDurationTicks(value) : Integer.parseInt(value));
                return true;
            }

            return setSimpleValue(option, value);
        } catch (IllegalArgumentException | ArithmeticException exception) {
            return false;
        }
    }

    private static boolean isDurationOption(String key) {
        return key.endsWith("duration") || key.endsWith("shrink-time");
    }

    private static int parseDurationTicks(String input) {
        String value = input.toLowerCase(Locale.ROOT).trim();

        if (value.isEmpty()) throw new IllegalArgumentException("Duration cannot be empty");

        char suffix = value.charAt(value.length() - 1);

        if (Character.isDigit(suffix)) return Integer.parseInt(value);

        int amount = Integer.parseInt(value.substring(0, value.length() - 1));

        if (amount < 0) throw new IllegalArgumentException("Duration cannot be negative");

        return switch (suffix) {
            case 't' -> amount;
            case 's' -> Math.multiplyExact(amount, TickConverter.TICKS_PER_SECOND);
            case 'm' -> TickConverter.minutesToTicks(amount);
            case 'h' -> TickConverter.hoursToTicks(amount);
            default -> throw new IllegalArgumentException("Unknown duration suffix");
        };
    }

    private static boolean parseBoolean(String value) {
        if (value.equalsIgnoreCase("true")) return true;
        if (value.equalsIgnoreCase("false")) return false;

        throw new IllegalArgumentException("Expected true or false");
    }

    private static String formatOptionValue(String key, Object value) {
        if (value instanceof World world) return world.getName();
        if (value instanceof UHC.MeetupDuration duration) return duration.isInfinite() ? "infinite" : formatTicks(duration.ticks());
        if (value instanceof Integer ticks && isDurationOption(key)) return formatTicks(ticks);

        return String.valueOf(value);
    }

    private static String formatTicks(long ticks) {
        long totalSeconds = Math.max(0L, ticks) / TickConverter.TICKS_PER_SECOND;
        long hours = totalSeconds / 3600L;
        long minutes = totalSeconds % 3600L / 60L;
        long seconds = totalSeconds % 60L;

        if (hours > 0L) return "%dh %dm %ds".formatted(hours, minutes, seconds);
        if (minutes > 0L) return "%dm %ds".formatted(minutes, seconds);

        return "%ds".formatted(seconds);
    }

    private static void sendLine(CommandSender sender, String label, String value) {
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<dark_gray> • <gray>" + label + ": <white>" + value));
    }

    private static void sendFailure(CommandSender sender, IllegalStateException exception) {
        ChatMessenger.sendMessage(sender, "<red>" + exception.getMessage() + ".");
    }

    @SuppressWarnings("unchecked")
    private static void restoreValue(Option<?> option, Object value) {
        ((Option<Object>) option).setValue(value);
    }
}