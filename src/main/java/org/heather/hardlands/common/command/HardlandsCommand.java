package org.heather.hardlands.common.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import org.heather.hardlands.Hardlands;
import org.heather.hardlands.common.menu.screen.MainMenu;
import org.heather.hardlands.core.option.Option;
import org.heather.hardlands.common.util.ChatMessenger;
import org.heather.hardlands.common.util.formatter.TickConverter;
import org.heather.hardlands.game.GameController;
import org.heather.hardlands.scenario.ScenarioManager;
import org.heather.hardlands.scenario.ScenarioDefinition;
import org.heather.hardlands.scenario.ScenarioModule;
import org.heather.hardlands.core.option.OptionDataType;
import org.heather.hardlands.world.border.WorldBorderController;
import org.heather.hardlands.world.pregen.PregenerationController;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.heather.hardlands.world.pregen.PregenerationState;

import java.util.Locale;

@RequiredArgsConstructor
@CommandAlias("hardlands|hl")
@CommandPermission("hardlands.admin")
public class HardlandsCommand extends BaseCommand {

    private final Hardlands plugin;

    @Default
    private void onHardlands(Player player) {
        MainMenu.INSTANCE.open(player);
    }

    @Subcommand("scenarios list")
    private void onScenariosList(CommandSender sender) {
        ScenarioManager controller = this.plugin.getScenarioManager();
        ScenarioDefinition[] definitions = ScenarioDefinition.values();

        ChatMessenger.sendMessage(sender, "<gray>Scenarios:");

        for (int i = 0; i < definitions.length; i++) {
            ScenarioDefinition definition = definitions[i];
            String state = controller.isScenarioActive(definition) ? "<green>active" : "<red>inactive";
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray>" + (i + 1) + ". <yellow>" + definition.getIdentifier() + " <gray>- " + state));
        }
    }

    @Subcommand("scenarios enable")
    @CommandCompletion("@registered_scenarios")
    private void onScenariosEnable(CommandSender sender, @Single String identifier) {
        if (!this.canModifyScenarios(sender)) return;

        ScenarioDefinition definition = this.findScenario(sender, identifier);
        if (definition == null) return;

        ChatMessenger.sendMessage(sender, this.plugin.getScenarioManager().enableScenario(definition)
                ? "<green>Scenario '" + identifier + "' enabled."
                : "<red>Scenario '" + identifier + "' is already active.");
    }

    @Subcommand("scenarios disable")
    @CommandCompletion("@active_scenarios")
    private void onScenariosDisable(CommandSender sender, @Single String identifier) {
        if (!this.canModifyScenarios(sender)) return;

        ScenarioDefinition definition = this.findScenario(sender, identifier);
        if (definition == null) return;

        ChatMessenger.sendMessage(sender, this.plugin.getScenarioManager().disableScenario(definition)
                ? "<green>Scenario '" + definition.getIdentifier() + "' disabled."
                : "<red>Scenario '" + definition.getIdentifier() + "' is not active.");
    }

    @Subcommand("scenarios option")
    @CommandCompletion("@registered_scenarios @scenario_options")
    private void onScenarioOption(CommandSender sender, @Single String identifier, @Single String key, @Single String value) {
        if (!this.canModifyScenarios(sender)) return;

        ScenarioDefinition definition = this.findScenario(sender, identifier);
        if (definition == null) return;

        ScenarioModule module = this.plugin.getScenarioManager().getRegisteredScenario(definition);
        Option<?> option = module.getOption(key);

        if (option == null) {
            ChatMessenger.sendMessage(sender, "<red>Option '" + key + "' not found.");
            return;
        }

        if (!setSimpleValue(option, value)) {
            ChatMessenger.sendMessage(sender, "<red>Invalid value '" + value + "' for '" + key + "'.");
            return;
        }

        ChatMessenger.sendMessage(sender, "<green>Option '" + key + "' set to <yellow>" + formatOptionValue(key, option.getValue()) + "<green>.");
    }

    @Subcommand("uhc")
    private void onUhcHelp(CommandSender sender) {
        ChatMessenger.sendMessage(sender, "<gray>UHC commands:");

        sendLine(sender, "/hardlands uhc options list", "View the configuration");
        sendLine(sender, "/hardlands uhc options set <key> <value>", "Change an option");
        sendLine(sender, "/hardlands uhc prepare", "Prepare and pregenerate the world");
        sendLine(sender, "/hardlands uhc prepare cancel", "Cancel world preparation");
        sendLine(sender, "/hardlands uhc start", "Start the prepared game");
        sendLine(sender, "/hardlands uhc next", "Advance the current phase");
        sendLine(sender, "/hardlands uhc status", "View the current state");
        sendLine(sender, "/hardlands uhc stop", "Stop the current game");
        sendLine(sender, "/hardlands uhc reset", "Reset the game");
    }

    @Subcommand("uhc prepare")
    private void onUhcPrepare(CommandSender sender) {
        GameController game = this.plugin.getGameController();

        if (game.isGameRunning()) {
            ChatMessenger.sendMessage(sender, "<red>The UHC is already running.");
            return;
        }

        try {
            this.plugin.getWorldManager().startPregeneration();
            ChatMessenger.sendMessage(sender, "<green>World pregeneration started with Chunky.");
        } catch (IllegalStateException | IllegalArgumentException exception) {
            sendFailure(sender, exception);
        }
    }

    @Subcommand("uhc prepare cancel")
    private void onUhcPrepareCancel(CommandSender sender) {
        try {
            this.plugin.getWorldManager().cancelPregeneration();
            ChatMessenger.sendMessage(sender, "<green>World pregeneration cancelled.");
        } catch (IllegalStateException exception) {
            sendFailure(sender, exception);
        }
    }

    @Subcommand("uhc start")
    private void onUhcStart(CommandSender sender) {
        GameController game = this.plugin.getGameController();

        try {
            game.startGame();
            ChatMessenger.sendMessage(sender, "<green>UHC started in the <yellow>" + game.getCurrentPhase().getDisplayName() + "<green> phase.");
        } catch (IllegalStateException exception) {
            sendFailure(sender, exception);
        }
    }

    @Subcommand("uhc stop")
    private void onUhcStop(CommandSender sender) {
        try {
            this.plugin.getGameController().stopGame();
            ChatMessenger.sendMessage(sender, "<green>UHC stopped.");
        } catch (IllegalStateException exception) {
            sendFailure(sender, exception);
        }
    }

    @Subcommand("uhc reset")
    private void onUhcReset(CommandSender sender) {
        try {
            this.plugin.getGameController().resetGame();
            ChatMessenger.sendMessage(sender, "<green>UHC reset to the lobby. World pregeneration must be run again.");
        } catch (IllegalStateException exception) {
            sendFailure(sender, exception);
        }
    }

    @Subcommand("uhc next")
    private void onUhcNext(CommandSender sender) {
        GameController game = this.plugin.getGameController();

        try {
            game.advanceGamePhase();
            ChatMessenger.sendMessage(sender, "<green>Advanced to <yellow>" + game.getCurrentPhase().getDisplayName() + "<green>.");
        } catch (IllegalStateException exception) {
            sendFailure(sender, exception);
        }
    }

    @Subcommand("uhc status")
    private void onUhcStatus(CommandSender sender) {
        GameController game = this.plugin.getGameController();
        PregenerationController pregeneration = this.plugin.getWorldManager().getPregenerationController();

        ChatMessenger.sendMessage(sender, "<gray>UHC status:");

        sendLine(sender, "Phase", game.getCurrentPhase().getDisplayName());
        sendLine(sender, "Running", game.isGameRunning() ? "<green>yes" : "<red>no");
        sendLine(sender, "PvP", game.isPvpEnabled() ? "<green>enabled" : "<red>disabled");
        sendLine(sender, "Pregeneration", pregeneration.getState().getDisplayName());
        sendLine(sender, "Configuration", game.isConfigurationValid() ? "<green>valid" : "<red>invalid");
        sendLine(sender, "Active scenarios", String.valueOf(this.plugin.getScenarioManager().getActiveScenarios().size()));

        if (pregeneration.getActiveRequest() != null) {
            sendLine(sender, "Preparing world", pregeneration.getActiveRequest().worldName());
            sendLine(sender, "Progress", "%.1f%%".formatted(pregeneration.getProgress()));
        }
    }

    @Subcommand("uhc options list")
    private void onUhcOptionsList(CommandSender sender) {
        GameController game = this.plugin.getGameController();
        WorldBorderController border = this.plugin.getWorldManager().getBorderController();

        ChatMessenger.sendMessage(sender, "<gray>UHC options:");
        game.getRegisteredOptions().forEach((key, option) ->
                sendLine(sender, key, formatOptionValue(key, option.getValue())));

        ChatMessenger.sendMessage(sender, "<gray>World border options:");
        border.getRegisteredOptions().forEach((key, option) ->
                sendLine(sender, key, formatOptionValue(key, option.getValue())));
    }

    @Subcommand("uhc options set")
    @CommandCompletion("@uhc_options")
    private void onUhcOptionSet(CommandSender sender, @Single String key, @Single String value) {
        GameController game = this.plugin.getGameController();

        if (game.isGameRunning()) {
            ChatMessenger.sendMessage(sender, "<red>UHC options cannot be changed while the game is running.");
            return;
        }

        Option<?> option = game.getOption(key);
        boolean borderOption = false;

        if (option == null) {
            option = this.plugin.getWorldManager().getBorderController().getOption(key);
            borderOption = option != null;
        }

        if (option == null) {
            ChatMessenger.sendMessage(sender, "<red>UHC option '" + key + "' not found.");
            return;
        }

        if (borderOption && this.plugin.getWorldManager().getPregenerationController().getState() != PregenerationState.IDLE) {
            ChatMessenger.sendMessage(sender, "<red>World border options are locked after pregeneration starts. Reset the UHC first.");
            return;
        }

        Object previousValue = option.getValue();

        if (!setGameValue(option, key, value) || !game.isConfigurationValid()) {
            restoreValue(option, previousValue);
            ChatMessenger.sendMessage(sender, "<red>Invalid value '" + value + "' for '" + key + "'.");
            return;
        }

        ChatMessenger.sendMessage(sender, "<green>Option '" + key + "' set to <yellow>" + formatOptionValue(key, option.getValue()) + "<green>.");
    }

    private boolean canModifyScenarios(CommandSender sender) {
        if (!this.plugin.getGameController().isGameRunning()) return true;

        ChatMessenger.sendMessage(sender, "<red>Scenarios cannot be changed while the UHC is running.");
        return false;
    }

    private ScenarioDefinition findScenario(CommandSender sender, String identifier) {
        ScenarioDefinition definition = ScenarioDefinition.findByIdentifier(identifier).orElse(null);

        if (definition == null) {
            ChatMessenger.sendMessage(sender, "<red>Scenario '" + identifier + "' not found.");
        }

        return definition;
    }

    private static boolean setSimpleValue(Option<?> option, String value) {
        Object previousValue = option.getValue();

        try {
            Object parsedValue = switch (option.getDataType()) {
                case BOOLEAN -> parseBoolean(value);
                case INTEGER -> Integer.parseInt(value);
                case FLOAT -> Float.parseFloat(value);
                case DOUBLE -> Double.parseDouble(value);
                case LONG -> Long.parseLong(value);
                case STRING -> value;
                default -> null;
            };

            if (parsedValue == null) return false;

            option.setValue(parsedValue);

            if (option.isValid()) return true;
        } catch (IllegalArgumentException ignored) {
        }

        restoreValue(option, previousValue);
        return false;
    }

    private static boolean setGameValue(Option<?> option, String key, String value) {
        Object previousValue = option.getValue();

        try {
            if (key.equals("meetup-duration")) {
                option.setValue(value.equalsIgnoreCase("infinite")
                        ? GameController.MeetupDuration.INFINITE
                        : new GameController.MeetupDuration(parseDurationTicks(value)));
            } else if (option.getDataType() == OptionDataType.INTEGER && isDurationOption(key)) {
                option.setValue(parseDurationTicks(value));
            } else {
                return setSimpleValue(option, value);
            }

            if (option.isValid()) return true;
        } catch (IllegalArgumentException | ArithmeticException ignored) {
        }

        restoreValue(option, previousValue);
        return false;
    }

    private static boolean isDurationOption(String key) {
        return key.endsWith("duration");
    }

    private static int parseDurationTicks(String input) {
        String value = input.toLowerCase(Locale.ROOT).trim();

        if (value.isEmpty()) {
            throw new IllegalArgumentException("Duration cannot be empty");
        }

        char suffix = value.charAt(value.length() - 1);

        if (Character.isDigit(suffix)) {
            int ticks = Integer.parseInt(value);

            if (ticks < 0) {
                throw new IllegalArgumentException("Duration cannot be negative");
            }

            return ticks;
        }

        int amount = Integer.parseInt(value.substring(0, value.length() - 1));

        if (amount < 0) {
            throw new IllegalArgumentException("Duration cannot be negative");
        }

        return switch (suffix) {
            case 't' -> amount;
            case 's' -> Math.multiplyExact(amount, TickConverter.TICKS_PER_SECOND);
            case 'm' -> Math.multiplyExact(amount, TickConverter.SECONDS_PER_MINUTE * TickConverter.TICKS_PER_SECOND);
            case 'h' -> Math.multiplyExact(amount, TickConverter.MINUTES_PER_HOUR * TickConverter.SECONDS_PER_MINUTE * TickConverter.TICKS_PER_SECOND);
            default -> throw new IllegalArgumentException("Unknown duration suffix");
        };
    }

    private static boolean parseBoolean(String value) {
        if (value.equalsIgnoreCase("true")) return true;
        if (value.equalsIgnoreCase("false")) return false;
        throw new IllegalArgumentException("Expected true or false");
    }

    private static String formatOptionValue(String key, Object value) {
        return switch (value) {
            case null -> "N/A";
            case GameController.MeetupDuration duration -> duration.isInfinite() ? "infinite" : formatTicks(duration.ticks());
            case Integer ticks when isDurationOption(key) -> formatTicks(ticks);
            default -> String.valueOf(value);
        };
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

    private static void sendFailure(CommandSender sender, RuntimeException exception) {
        ChatMessenger.sendMessage(sender, "<red>" + exception.getMessage() + ".");
    }

    private static void restoreValue(Option<?> option, Object value) {
        if (value == null) {
            option.clearValue();
        } else {
            option.setValue(value);
        }
    }
}