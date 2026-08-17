package org.heather.hardlands.module.phase;

import org.heather.hardlands.core.ThreadScheduler;
import org.heather.hardlands.core.config.Option;
import org.heather.hardlands.annotation.ConfigOption;
import org.heather.hardlands.annotation.ConfigurationSpec;

@ConfigurationSpec(
        identifier = "timer",
        options = {
                @ConfigOption(name = "pvpStartMinute", type = Integer.class),
                @ConfigOption(name = "borderStartMinute", type = Integer.class),
                @ConfigOption(name = "meetupStartMinute", type = Integer.class),
                @ConfigOption(name = "deathmatchStartMinute", type = Integer.class)
        }
)
public final class PhaseTimer extends PhaseTimerConfiguration {

    private static final long TICKS_PER_MINUTE = 20L * 60L;

    private final ThreadScheduler scheduler;

    public PhaseTimer(ThreadScheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void scheduleForOption(Runnable runnable, Option<Integer> option) {
        long ticks = option.getValue() * TICKS_PER_MINUTE;
        this.scheduler.scheduleSync(runnable, ticks);
    }

    @Override
    protected boolean isConfigurationValid() {
        int pvp = this.pvpStartMinute.getValue();
        int border = this.borderStartMinute.getValue();
        int meetup = this.meetupStartMinute.getValue();
        int deathmatch = this.deathmatchStartMinute.getValue();

        return pvp >= 0
                && pvp < border
                && border < meetup
                && meetup < deathmatch;
    }
}