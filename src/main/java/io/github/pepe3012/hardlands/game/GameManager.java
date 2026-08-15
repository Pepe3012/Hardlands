package io.github.pepe3012.hardlands.game;

import io.github.pepe3012.hardlands.Hardlands;
import io.github.pepe3012.hardlands.data.option.Option;
import io.github.pepe3012.hardlands.data.option.OptionValidators;

import java.util.List;

public final class GameManager extends OptionHolder {

    private final Option<Integer> gracePeriodOption = super.createOption("grace-period", OptionValidators.Integers.NON_NEGATIVE);
    private final Option<Integer> survivalDurationOption = super.createOption("survival-duration", OptionValidators.Integers.POSITIVE);
    private final Option<MeetupDuration> meetupDurationOption = super.createOption("meetup-duration", MeetupDuration.class);

    private final Hardlands plugin;

    private GamePhase currentPhase = GamePhase.IDLE;

    public GameManager(Hardlands plugin) {
        this.plugin = plugin;
    }

    public GamePhase getCurrentPhase() {
        return this.currentPhase;
    }

    public void startGame() {
        if (this.currentPhase != GamePhase.PRE_GAME) {
            throw new IllegalStateException("The game must be started only at phase: %s, but tried to at: %s".formatted(GamePhase.PRE_GAME.name(), this.currentPhase.name()));
        }

        if (!this.plugin.getWorldManager().isPregenerationCompleted()) {
            throw new IllegalStateException("World pregeneration has not been completed");
        }

        this.requireValidConfiguration();

        //this.plugin.getWorldManager().shrinkBorderForSurvival(); must be done at pregeneration
        this.startPhase(GamePhase.SURVIVAL, this.gracePeriodOption.getValue());
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

        this.scheduler.cancelScheduledTask();
        this.plugin.getWorldManager().resetPregeneration();
        this.currentPhase = GamePhase.IDLE;
    }

    public void advanceGamePhase() {
        if (!this.currentPhase.canAdvance()) {
            throw new IllegalStateException("The " + this.currentPhase.getDisplayName() + " phase cannot be advanced");
        }

        this.scheduler.cancelScheduledTask();

        switch (this.currentPhase) {
            case GRACE_PERIOD -> this.startPhase(GamePhase.PVE, (long) this.survivalDurationOption.getValue() - this.gracePeriodOption.getValue());

            case PVE -> this.startPhase(GamePhase.BORDER_SHRINK, this.plugin.getWorldManager().handleBorderForMeetup());

            case BORDER_SHRINK -> this.startMeetup();

            case MEETUP -> {
                this.currentPhase = GamePhase.DEATHMATCH;
                this.plugin.getWorldManager().handleBorderForDeathmatch();
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
        if (!super.areOptionsValid()) {
            return false;
        }

        return this.plugin.getWorldManager().isBorderConfigurationValid()
                && this.gracePeriodOption.getValue() <= this.survivalDurationOption.getValue();
    }

    public List<String> getConfigurationOptionKeys() {
        return List.copyOf(super.getOptions().keySet());
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
        this.scheduler.cancelScheduledTask();
        this.currentPhase = GamePhase.FINISHED;
    }

    private void schedulePhaseAdvance(long delay) {
        this.scheduler.scheduleTask(this::advanceGamePhase, delay);
    }

    private void requireValidConfiguration() {
        if (!super.areOptionsValid()) {
            throw new IllegalStateException("The game configuration is invalid");
        }

        if (!this.plugin.getWorldManager().isBorderConfigurationValid()) {
            throw new IllegalStateException("The world border configuration is invalid");
        }

        if (this.gracePeriodOption.getValue() > this.survivalDurationOption.getValue()) {
            throw new IllegalStateException("Pact duration cannot exceed survival duration");
        }
    }

    public record MeetupDuration(int ticks) {

        public static final MeetupDuration INFINITE = new MeetupDuration(-1);

        public MeetupDuration {
            if (ticks < -1) {
                Duration.
                throw new IllegalArgumentException("Meetup duration cannot be less than -1");
            }
        }

        public boolean isInfinite() {
            return this.ticks == INFINITE.ticks;
        }
    }
}