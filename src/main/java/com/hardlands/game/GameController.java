package com.hardlands.game;

import com.hardlands.HardlandsPlugin;
import com.hardlands.common.option.Option;
import com.hardlands.common.option.OptionHolder;
import com.hardlands.common.option.OptionValidators;
import com.hardlands.common.util.formatter.TickConverter;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.stream.Stream;

public final class GameController extends OptionHolder {

    private final Option<Integer> pactDurationOption = createOption("pact-duration", Integer.class, OptionValidators.Integers.NON_NEGATIVE);
    private final Option<Integer> survivalDurationOption = createOption("survival-duration", Integer.class, OptionValidators.Integers.POSITIVE);
    private final Option<MeetupDuration> meetupDurationOption = createOption("meetup-duration", MeetupDuration.class);

    private final HardlandsPlugin plugin;

    @Getter private GamePhase currentPhase = GamePhase.LOBBY;

    private BukkitTask phaseTask;

    public GameController(HardlandsPlugin plugin) {
        this.plugin = plugin;
    }

    public void startGame() {
        if (currentPhase != GamePhase.LOBBY) {
            throw new IllegalStateException("The game cannot start from the " + currentPhase.getDisplayName() + " phase");
        }

        if (!plugin.getWorldManager().isWorldPregenerationCompleted()) {
            throw new IllegalStateException("World pregeneration has not been completed");
        }

        requireValidConfiguration();
        plugin.getWorldManager().initializeWorldBorderForSurvival();

        currentPhase = GamePhase.SURVIVAL_GRACE_PERIOD;
        schedulePhaseAdvance(pactDurationOption.getValue());
    }

    public void stopGame() {
        if (!isGameRunning()) {
            throw new IllegalStateException("The game is not running");
        }

        cancelPhaseTask();
        currentPhase = GamePhase.FINISHED;
    }

    public void resetGame() {
        if (isGameRunning()) {
            throw new IllegalStateException("The game cannot be reset while running");
        }

        cancelPhaseTask();
        plugin.getWorldManager().resetWorldPregeneration();
        currentPhase = GamePhase.LOBBY;
    }

    public void advanceGamePhase() {
        if (!isGameRunning()) {
            throw new IllegalStateException("The " + currentPhase.getDisplayName() + " phase cannot be advanced");
        }

        cancelPhaseTask();

        switch (currentPhase) {
            case SURVIVAL_GRACE_PERIOD -> {
                currentPhase = GamePhase.SURVIVAL_PVP;
                schedulePhaseAdvance((long) survivalDurationOption.getValue() - pactDurationOption.getValue());
            }

            case SURVIVAL_PVP -> {
                currentPhase = GamePhase.BORDER_SHRINK;
                schedulePhaseAdvance(plugin.getWorldManager().shrinkWorldBorderForMeetup());
            }

            case BORDER_SHRINK -> {
                currentPhase = GamePhase.MEETUP;
                MeetupDuration duration = meetupDurationOption.getValue();

                if (!duration.isInfinite()) {
                    schedulePhaseAdvance(duration.ticks());
                }
            }

            case MEETUP -> {
                currentPhase = GamePhase.DEATHMATCH;
                plugin.getWorldManager().shrinkWorldBorderForDeathmatch();
            }

            case DEATHMATCH -> currentPhase = GamePhase.FINISHED;

            default -> {
                throw new IllegalStateException("The " + currentPhase.getDisplayName() + " phase cannot be advanced");
            }
        }
    }

    public boolean isGameRunning() {
        return currentPhase != GamePhase.LOBBY && currentPhase != GamePhase.FINISHED;
    }

    public boolean isPvpEnabled() {
        return currentPhase.isPvpEnabled();
    }

    public boolean isConfigurationValid() {
        return areOptionsValid()
                && plugin.getWorldManager().isWorldBorderConfigurationValid()
                && pactDurationOption.getValue() <= survivalDurationOption.getValue();
    }

    public List<String> getConfigurationOptionKeys() {
        return Stream.concat(getRegisteredOptions().keySet().stream(), plugin.getWorldManager().getBorderController().getRegisteredOptions().keySet().stream()).distinct().toList();
    }

    private void requireValidConfiguration() {
        if (!areOptionsValid()) {
            throw new IllegalStateException("The game configuration is invalid");
        }

        if (!plugin.getWorldManager().isWorldBorderConfigurationValid()) {
            throw new IllegalStateException("The world border configuration is invalid");
        }

        if (pactDurationOption.getValue() > survivalDurationOption.getValue()) {
            throw new IllegalStateException("Pact duration cannot exceed survival duration");
        }
    }

    private void schedulePhaseAdvance(long delay) {
        if (delay < 0L) {
            throw new IllegalArgumentException("Phase delay cannot be negative");
        }

        cancelPhaseTask();

        phaseTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            phaseTask = null;
            advanceGamePhase();
        }, delay);
    }

    private void cancelPhaseTask() {
        if (phaseTask != null) {
            phaseTask.cancel();
        }

        phaseTask = null;
    }

    public record MeetupDuration(int ticks) {

        public static final MeetupDuration INFINITE = new MeetupDuration(-1);

        public MeetupDuration {
            if (ticks < -1) {
                throw new IllegalArgumentException("Ticks cannot be less than -1");
            }
        }

        public static MeetupDuration ofMinutes(int minutes) {
            if (minutes < 0) {
                throw new IllegalArgumentException("Minutes cannot be negative");
            }

            return new MeetupDuration(TickConverter.minutesToTicks(minutes));
        }

        public boolean isInfinite() {
            return ticks == INFINITE.ticks;
        }
    }
}