package com.hardlands.uhc;

import com.hardlands.HardlandsPlugin;
import com.hardlands.util.option.Option;
import com.hardlands.util.option.OptionContainer;
import com.hardlands.util.option.OptionValidators;
import com.hardlands.util.TickConverter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public final class UHC {

    @Getter private final WorldBorderManager worldBorderManager = new WorldBorderManager();
    @Getter private final PreparationManager preparationManager = new PreparationManager(this.worldBorderManager);
    @Getter private final OptionContainer optionContainer = new OptionContainer();

    private final Option<Integer> pactDurationOption = this.optionContainer.create("pact-duration", TickConverter.minutesToTicks(15), OptionValidators.Integers.NON_NEGATIVE);
    private final Option<Integer> survivalDurationOption = this.optionContainer.create("survival-duration", TickConverter.minutesToTicks(30), OptionValidators.Integers.POSITIVE);
    private final Option<MeetupDuration> meetupDurationOption = this.optionContainer.create("meetup-duration", MeetupDuration.ofMinutes(15));

    private final HardlandsPlugin plugin;

    @Getter private Phase phase = Phase.LOBBY;

    private BukkitTask currentTask;
    private long phaseEndsAtMillis = -1L;

    public UHC(HardlandsPlugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (this.phase != Phase.LOBBY) throw new IllegalStateException("The UHC cannot start from the " + this.phase.getDisplayName() + " phase");
        if (!this.preparationManager.isCompleted()) throw new IllegalStateException("The UHC preparation has not been completed");

        this.requireValidConfiguration();
        this.transitionToPhase(Phase.SURVIVAL_GRACE_PERIOD);
    }

    public void stop() {
        if (!this.isRunning()) throw new IllegalStateException("The UHC is not running");

        this.transitionToPhase(Phase.FINISHED);
    }

    public void reset() {
        if (this.isRunning()) throw new IllegalStateException("The UHC cannot be reset while running");

        this.cancelCurrentTask();
        this.preparationManager.resetPreparation();
        this.phase = Phase.LOBBY;
    }

    public void advancePhase() {
        Phase nextPhase = switch (this.phase) {
            case SURVIVAL_GRACE_PERIOD -> Phase.SURVIVAL_PVP;
            case SURVIVAL_PVP -> Phase.BORDER_SHRINK;
            case BORDER_SHRINK -> Phase.MEETUP;
            case MEETUP -> Phase.DEATHMATCH;
            default -> throw new IllegalStateException("The " + this.phase.getDisplayName() + " phase cannot be advanced");
        };

        this.transitionToPhase(nextPhase);
    }

    public void transitionToPhase(Phase nextPhase) {
        if (!this.canTransitionTo(nextPhase)) throw new IllegalStateException("Cannot transition from " + this.phase.getDisplayName() + " to " + nextPhase.getDisplayName());

        this.cancelCurrentTask();
        this.phase = nextPhase;

        switch (nextPhase) {
            case SURVIVAL_GRACE_PERIOD -> this.enterSurvivalGracePeriod();
            case SURVIVAL_PVP -> this.enterSurvivalPvp();
            case BORDER_SHRINK -> this.enterBorderShrink();
            case MEETUP -> this.enterMeetup();
            case DEATHMATCH -> this.enterDeathmatch();
            case FINISHED -> this.enterFinished();
        }
    }

    public boolean isRunning() {
        return this.phase != Phase.LOBBY && this.phase != Phase.FINISHED;
    }

    public boolean isFinished() {
        return this.phase == Phase.FINISHED;
    }

    public boolean isPvpEnabled() {
        return this.phase.isPvpEnabled();
    }

    public boolean isConfigurationValid() {
        return this.optionContainer.validate() && this.worldBorderManager.validate() && this.pactDurationOption.getValue() <= this.survivalDurationOption.getValue();
    }

    public long getRemainingPhaseTicks() {
        return this.phaseEndsAtMillis < 0L ? -1L : Math.max(0L, (this.phaseEndsAtMillis - System.currentTimeMillis() + 49L) / 50L);
    }

    private void requireValidConfiguration() {
        if (!this.optionContainer.validate()) throw new IllegalStateException("The UHC configuration is invalid");
        if (!this.worldBorderManager.validate()) throw new IllegalStateException("The world border configuration is invalid");
        if (this.pactDurationOption.getValue() > this.survivalDurationOption.getValue()) throw new IllegalStateException("Pact duration cannot exceed survival duration");
    }

    private boolean canTransitionTo(Phase nextPhase) {
        if (nextPhase == Phase.FINISHED) return this.isRunning();

        return switch (this.phase) {
            case LOBBY -> nextPhase == Phase.SURVIVAL_GRACE_PERIOD;
            case SURVIVAL_GRACE_PERIOD -> nextPhase == Phase.SURVIVAL_PVP;
            case SURVIVAL_PVP -> nextPhase == Phase.BORDER_SHRINK;
            case BORDER_SHRINK -> nextPhase == Phase.MEETUP;
            case MEETUP -> nextPhase == Phase.DEATHMATCH;
            default -> false;
        };
    }

    private void scheduleDelayedTask(Runnable action, long delay) {
        if (delay < 0L) throw new IllegalArgumentException("Task delay cannot be negative");

        this.cancelCurrentTask();
        this.phaseEndsAtMillis = System.currentTimeMillis() + delay * 50L;
        this.currentTask = Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            this.currentTask = null;
            this.phaseEndsAtMillis = -1L;
            action.run();
        }, delay);
    }

    private void cancelCurrentTask() {
        if (this.currentTask != null) this.currentTask.cancel();

        this.currentTask = null;
        this.phaseEndsAtMillis = -1L;
    }

    private void enterSurvivalGracePeriod() {
        this.scheduleDelayedTask(() -> this.transitionToPhase(Phase.SURVIVAL_PVP), this.pactDurationOption.getValue());
    }

    private void enterSurvivalPvp() {
        this.scheduleDelayedTask(() -> this.transitionToPhase(Phase.BORDER_SHRINK), this.survivalDurationOption.getValue() - this.pactDurationOption.getValue());
    }

    private void enterBorderShrink() {
        this.scheduleDelayedTask(() -> this.transitionToPhase(Phase.MEETUP), this.worldBorderManager.shrinkForMeetup());
    }

    private void enterMeetup() {
        MeetupDuration duration = this.meetupDurationOption.getValue();

        if (!duration.isInfinite()) this.scheduleDelayedTask(() -> this.transitionToPhase(Phase.DEATHMATCH), duration.ticks());
    }

    private void enterDeathmatch() {
        this.worldBorderManager.shrinkForDeathmatch();
    }

    private void enterFinished() {
        this.cancelCurrentTask();
    }

    public record MeetupDuration(int ticks) {

        public static final MeetupDuration INFINITE = new MeetupDuration(-1);

        public MeetupDuration {
            if (ticks < -1) throw new IllegalArgumentException("Ticks cannot be less than -1");
        }

        public static MeetupDuration ofMinutes(int minutes) {
            if (minutes < 0) throw new IllegalArgumentException("Minutes cannot be negative");

            return new MeetupDuration(TickConverter.minutesToTicks(minutes));
        }

        public boolean isInfinite() {
            return this.ticks == INFINITE.ticks;
        }
    }

    @Getter
    @RequiredArgsConstructor
    public enum Phase {

        LOBBY("Lobby", false),
        SCATTERING("Scattering", false),
        SURVIVAL_GRACE_PERIOD("Survival Grace Period", false),
        SURVIVAL_PVP("Survival PvP", true),
        BORDER_SHRINK("Border Shrink", true),
        MEETUP("Meetup", true),
        DEATHMATCH("Deathmatch", true),
        FINISHED("Finished", false);

        private final String displayName;
        private final boolean pvpEnabled;
    }
}