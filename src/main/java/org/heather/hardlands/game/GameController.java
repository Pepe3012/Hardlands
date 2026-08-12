package org.heather.hardlands.game;

import lombok.Getter;
import org.heather.hardlands.Hardlands;
import org.heather.hardlands.common.util.formatter.TickConverter;
import org.heather.hardlands.core.option.Option;
import org.heather.hardlands.core.option.OptionHolder;
import org.heather.hardlands.core.option.OptionValidators;

import java.util.List;

public final class GameController extends OptionHolder {

    private final Option<Integer> pactDurationOption = super.createOption("pact-duration", OptionValidators.Integers.NON_NEGATIVE);
    private final Option<Integer> survivalDurationOption = super.createOption("survival-duration", OptionValidators.Integers.POSITIVE);
    private final Option<MeetupDuration> meetupDurationOption = super.createOption("meetup-duration", MeetupDuration.class);

    private final Hardlands plugin;
    private final GameTaskScheduler taskScheduler;

    @Getter private GamePhase currentPhase = GamePhase.LOBBY;

    public GameController(final Hardlands plugin) {
        this.plugin = plugin;
        this.taskScheduler = new GameTaskScheduler(plugin);
    }

    public void startGame() {
        if (this.currentPhase != GamePhase.LOBBY) {
            throw new IllegalStateException("The game cannot start from the " + this.currentPhase.getDisplayName() + " phase");
        }


        if (!this.plugin.getWorldManager().isPregenerationCompleted()) {
            throw new IllegalStateException("World pregeneration has not been completed");
        }

        this.requireValidConfiguration();

        this.plugin.getWorldManager().initializeSurvivalBorder();
        this.startPhase(GamePhase.SURVIVAL_GRACE_PERIOD, this.pactDurationOption.getValue());
    }

    public void stopGame() {
        if (!this.isGameRunning()) {
            throw new IllegalStateException("The game is not running");
        }

        this.finishGame();
    }

    public void resetGame() {
        if (this.isGameRunning()) {
            throw new IllegalStateException("The game cannot be reset while running");
        }

        this.taskScheduler.cancelScheduledTask();
        this.plugin.getWorldManager().resetPregeneration();
        this.currentPhase = GamePhase.LOBBY;
    }

    public void advanceGamePhase() {
        if (!this.currentPhase.canAdvance()) {
            throw new IllegalStateException("The " + this.currentPhase.getDisplayName() + " phase cannot be advanced");
        }

        this.taskScheduler.cancelScheduledTask();

        switch (this.currentPhase) {
            case SURVIVAL_GRACE_PERIOD -> this.startPhase(GamePhase.SURVIVAL_PVP, (long) this.survivalDurationOption.getValue() - this.pactDurationOption.getValue());

            case SURVIVAL_PVP -> this.startPhase(GamePhase.BORDER_SHRINK, this.plugin.getWorldManager().shrinkBorderForMeetup());

            case BORDER_SHRINK -> this.startMeetup();

            case MEETUP -> {
                this.currentPhase = GamePhase.DEATHMATCH;
                this.plugin.getWorldManager().shrinkBorderForDeathmatch();
            }

            case DEATHMATCH -> this.finishGame();

            default -> throw new IllegalStateException("The " + this.currentPhase.getDisplayName() + " phase cannot be advanced");
        }
    }

    public boolean isGameRunning() {
        return this.currentPhase.isRunning();
    }

    public boolean isPvpEnabled() {
        return this.currentPhase.isPvpEnabled();
    }

    public boolean isConfigurationValid() {
        return super.areOptionsValid()
                && this.plugin.getWorldManager().isBorderConfigurationValid()
                && this.pactDurationOption.getValue() <= this.survivalDurationOption.getValue();
    }

    public List<String> getConfigurationOptionKeys() {
        return super.getRegisteredOptions().keySet().stream().toList();
    }

    private void startMeetup() {
        this.currentPhase = GamePhase.MEETUP;

        MeetupDuration duration = this.meetupDurationOption.getValue();
        if (!duration.isInfinite()) {
            this.schedulePhaseAdvance(duration.ticks());
        }
    }

    private void startPhase(GamePhase phase, long duration) {
        this.currentPhase = phase;
        this.schedulePhaseAdvance(duration);
    }

    private void finishGame() {
        this.taskScheduler.cancelScheduledTask();
        this.currentPhase = GamePhase.FINISHED;
    }

    private void schedulePhaseAdvance(long delay) {
        this.taskScheduler.scheduleTask(this::advanceGamePhase, delay);
    }

    private void requireValidConfiguration() {
        if (!super.areOptionsValid()) {
            throw new IllegalStateException("The game configuration is invalid");
        }

        if (!this.plugin.getWorldManager().isBorderConfigurationValid()) {
            throw new IllegalStateException("The world border configuration is invalid");
        }

        if (this.pactDurationOption.getValue() > this.survivalDurationOption.getValue()) {
            throw new IllegalStateException("Pact duration cannot exceed survival duration");
        }
    }

    public record MeetupDuration(int ticks) {

        public static final MeetupDuration INFINITE = new MeetupDuration(-1);

        public static MeetupDuration ofMinutes(int minutes) {
            return new MeetupDuration(TickConverter.minutesToTicks(minutes));
        }

        public boolean isInfinite() {
            return this.equals(INFINITE);
        }
    }
}