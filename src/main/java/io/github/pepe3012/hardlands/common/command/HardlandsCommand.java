package io.github.pepe3012.hardlands.common.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import io.github.pepe3012.hardlands.Hardlands;
import io.github.pepe3012.hardlands.common.item.HardlandsItems;
import io.github.pepe3012.hardlands.common.util.ChatMessenger;
import io.github.pepe3012.hardlands.config.inventory.InventoryDefinition;
import io.github.pepe3012.hardlands.config.option.Option;
import io.github.pepe3012.hardlands.config.option.OptionDataType;
import io.github.pepe3012.hardlands.game.GameController;
import io.github.pepe3012.hardlands.scenario.ScenarioDefinition;
import io.github.pepe3012.hardlands.scenario.ScenarioManager;
import io.github.pepe3012.hardlands.scenario.ScenarioModule;
import io.github.pepe3012.hardlands.world.border.WorldBorderController;
import io.github.pepe3012.hardlands.world.pregen.PregenerationController;
import io.github.pepe3012.hardlands.world.pregen.PregenerationRequest;
import io.github.pepe3012.hardlands.world.pregen.PregenerationState;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

@CommandAlias("hardlands|hl")
@CommandPermission("hardlands.admin")
public final class HardlandsCommand extends BaseCommand {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private static final int TICKS_PER_SECOND = 20;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int MINUTES_PER_HOUR = 60;

    private final Hardlands plugin;

    public HardlandsCommand(Hardlands plugin) {
        this.plugin = plugin;
    }

    @Default
    private void onHardlands(Player player) {
        InventoryDefinition.MAIN.openInventory(player);
    }

    @Subcommand("give")
    @CommandCompletion("@hardlands_items")
    private void onGive(Player player, @Single String identifier) {
        HardlandsItems item = HardlandsItems.find(identifier).orElse(null);

        if (item == null) {
            ChatMessenger.sendMessage(player, "<red>Item '" + identifier + "' not found.");
            return;
        }

        player.getInventory().addItem(item.build());
        ChatMessenger.sendMessage(player, "<green>Received <yellow>" + item.getIdentifier() + "<green>.");
    }

    @Subcommand("scenarios list")
    private void onScenariosList(CommandSender sender) {
        ScenarioManager manager = this.plugin.getScenarioManager();
        List<ScenarioDefinition> definitions = manager.getRegisteredScenarioDefinitions();

        ChatMessenger.sendMessage(sender, "<gray>Scenarios:");

        for (int index = 0; index < definitions.size(); index++) {
            ScenarioDefinition definition = definitions.get(index);
            String state = manager.isScenarioActive(definition) ? "<green>active" : "<red>inactive";

            sender.sendMessage(MINI_MESSAGE.deserialize(
                    "<gray>" + (index + 1) + ". <yellow>" + definition.getIdentifier() + " <gray>- " + state
            ));
        }
    }

    @Subcommand("scenarios enable")
    @CommandCompletion("@registered_scenarios")
    private void onScenariosEnable(CommandSender sender, @Single String identifier) {
        if (!this.canModifyScenarios(sender)) {
            return;
        }

        ScenarioDefinition definition = this.findScenario(sender, identifier);

        if (definition == null) {
            return;
        }

        ChatMessenger.sendMessage(sender, this.plugin.getScenarioManager().enableScenario(definition)
                ? "<green>Scenario '" + definition.getIdentifier() + "' enabled."
                : "<red>Scenario '" + definition.getIdentifier() + "' is already active.");
    }

    @Subcommand("scenarios disable")
    @CommandCompletion("@active_scenarios")
    private void onScenariosDisable(CommandSender sender, @Single String identifier) {
        if (!this.canModifyScenarios(sender)) {
            return;
        }

        ScenarioDefinition definition = this.findScenario(sender, identifier);

        if (definition == null) {
            return;
        }

        ChatMessenger.sendMessage(sender, this.plugin.getScenarioManager().disableScenario(definition)
                ? "<green>Scenario '" + definition.getIdentifier() + "' disabled."
                : "<red>Scenario '" + definition.getIdentifier() + "' is not active.");
    }

    @Subcommand("scenarios option")
    @CommandCompletion("@registered_scenarios @scenario_options *")
    private void onScenarioOption(
            CommandSender sender,
            @Single String identifier,
            @Single String key,
            @Single String value
    ) {
        if (!this.canModifyScenarios(sender)) {
            return;
        }

        ScenarioDefinition definition = this.findScenario(sender, identifier);

        if (definition == null) {
            return;
        }

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

        ChatMessenger.sendMessage(sender, "<green>Option '" + key + "' set to <yellow>"
                + formatOptionValue(key, option.getValue()) + "<green>.");
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
            ChatMessenger.sendMessage(sender, "<green>UHC started in the <yellow>"
                    + game.getCurrentPhase().getDisplayName() + "<green> phase.");
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
            ChatMessenger.sendMessage(sender, "<green>UHC reset to the lobby.");
        } catch (IllegalStateException exception) {
            sendFailure(sender, exception);
        }
    }

    @Subcommand("uhc next")
    private void onUhcNext(CommandSender sender) {
        GameController game = this.plugin.getGameController();

        try {
            game.advanceGamePhase();
            ChatMessenger.sendMessage(sender, "<green>Advanced to <yellow>"
                    + game.getCurrentPhase().getDisplayName() + "<green>.");
        } catch (IllegalStateException exception) {
            sendFailure(sender, exception);
        }
    }

    @Subcommand("uhc status")
    private void onUhcStatus(CommandSender sender) {
        GameController game = this.plugin.getGameController();
        PregenerationController pregeneration = this.plugin.getWorldManager().getPregenerationController();
        PregenerationRequest request = pregeneration.getActiveRequest();

        ChatMessenger.sendMessage(sender, "<gray>UHC status:");
        sendLine(sender, "Phase", game.getCurrentPhase().getDisplayName());
        sendLine(sender, "Running", game.isGameRunning() ? "<green>yes" : "<red>no");
        sendLine(sender, "PvP", game.isPvpEnabled() ? "<green>enabled" : "<red>disabled");
        sendLine(sender, "Pregeneration", pregeneration.getState().getDisplayName());
        sendLine(sender, "Configuration", game.isConfigurationValid() ? "<green>valid" : "<red>invalid");
        sendLine(sender, "Active scenarios",
                String.valueOf(this.plugin.getScenarioManager().getActiveScenarios().size()));

        if (request != null) {
            sendLine(sender, "Preparing world", request.worldName());
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
    @CommandCompletion("@uhc_options *")
    private void onUhcOptionSet(CommandSender sender, @Single String key, @Single String value) {
        GameController game = this.plugin.getGameController();
        WorldBorderController border = this.plugin.getWorldManager().getBorderController();

        if (game.isGameRunning()) {
            ChatMessenger.sendMessage(sender, "<red>UHC options cannot be changed while the game is running.");
            return;
        }

        Option<?> option = game.getOption(key);
        boolean borderOption = option == null;

        if (borderOption) {
            option = border.getOption(key);
        }

        if (option == null) {
            ChatMessenger.sendMessage(sender, "<red>UHC option '" + key + "' not found.");
            return;
        }

        if (borderOption
                && this.plugin.getWorldManager().getPregenerationController().getState() != PregenerationState.IDLE) {
            ChatMessenger.sendMessage(
                    sender,
                    "<red>World border options are locked after pregeneration starts. Reset the UHC first."
            );
            return;
        }

        Object previousValue = option.getValue();

        if (!setGameValue(option, key, value)) {
            ChatMessenger.sendMessage(sender, "<red>Invalid value '" + value + "' for '" + key + "'.");
            return;
        }

        boolean configurationValid = borderOption
                ? border.isConfigurationValid()
                : game.isConfigurationValid();

        if (!configurationValid) {
            restoreValue(option, previousValue);
            ChatMessenger.sendMessage(sender, "<red>Invalid value '" + value + "' for '" + key + "'.");
            return;
        }

        ChatMessenger.sendMessage(sender, "<green>Option '" + key + "' set to <yellow>"
                + formatOptionValue(key, option.getValue()) + "<green>.");
    }

    private boolean canModifyScenarios(CommandSender sender) {
        if (!this.plugin.getGameController().isGameRunning()) {
            return true;
        }

        ChatMessenger.sendMessage(sender, "<red>Scenarios cannot be changed while the UHC is running.");
        return false;
    }

    private ScenarioDefinition findScenario(CommandSender sender, String identifier) {
        ScenarioDefinition definition = ScenarioDefinition.findByIdentifier(identifier).orElse(null);

        if (definition == null || !this.plugin.getScenarioManager().isScenarioRegistered(definition)) {
            ChatMessenger.sendMessage(sender, "<red>Scenario '" + identifier + "' not found.");
            return null;
        }

        return definition;
    }

    private static boolean setSimpleValue(Option<?> option, String value) {
        try {
            Object parsedValue = switch (option.getDataType()) {
                case BOOLEAN -> parseBoolean(value);
                case DOUBLE -> Double.parseDouble(value);
                case FLOAT -> Float.parseFloat(value);
                case INTEGER -> Integer.parseInt(value);
                case LONG -> Long.parseLong(value);
                case STRING -> value;
                default -> null;
            };

            if (parsedValue == null) {
                return false;
            }

            option.setValue(parsedValue);
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private static boolean setGameValue(Option<?> option, String key, String value) {
        try {
            if ("meetup-duration".equals(key)) {
                option.setValue(value.equalsIgnoreCase("infinite")
                        ? GameController.MeetupDuration.INFINITE
                        : new GameController.MeetupDuration(parseDurationTicks(value)));

                return true;
            }

            if (option.getDataType() == OptionDataType.INTEGER && isDurationOption(key)) {
                option.setValue(parseDurationTicks(value));
                return true;
            }

            return setSimpleValue(option, value);
        } catch (IllegalArgumentException | ArithmeticException exception) {
            return false;
        }
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
            case 's' -> Math.multiplyExact(amount, TICKS_PER_SECOND);
            case 'm' -> Math.multiplyExact(amount, SECONDS_PER_MINUTE * TICKS_PER_SECOND);
            case 'h' -> Math.multiplyExact(
                    amount,
                    MINUTES_PER_HOUR * SECONDS_PER_MINUTE * TICKS_PER_SECOND
            );
            default -> throw new IllegalArgumentException("Unknown duration suffix: " + suffix);
        };
    }

    private static boolean parseBoolean(String value) {
        if (value.equalsIgnoreCase("true")) {
            return true;
        }

        if (value.equalsIgnoreCase("false")) {
            return false;
        }

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
        long totalSeconds = Math.max(0L, ticks) / TICKS_PER_SECOND;
        long hours = totalSeconds / (MINUTES_PER_HOUR * SECONDS_PER_MINUTE);
        long minutes = totalSeconds % (MINUTES_PER_HOUR * SECONDS_PER_MINUTE) / SECONDS_PER_MINUTE;
        long seconds = totalSeconds % SECONDS_PER_MINUTE;

        if (hours > 0L) return "%dh %dm %ds".formatted(hours, minutes, seconds);
        if (minutes > 0L) return "%dm %ds".formatted(minutes, seconds);
        return "%ds".formatted(seconds);
    }

    private static void sendLine(CommandSender sender, String label, String value) {
        sender.sendMessage(MINI_MESSAGE.deserialize("<dark_gray> • <gray>" + label + ": <white>" + value));
    }

    private static void sendFailure(CommandSender sender, RuntimeException exception) {
        String message = exception.getMessage();
        ChatMessenger.sendMessage(sender, "<red>" + (message == null ? "Operation failed" : message) + ".");
    }

    private static void restoreValue(Option<?> option, Object value) {
        if (value == null) {
            option.clearValue();
            return;
        }

        option.setValue(value);
    }
}