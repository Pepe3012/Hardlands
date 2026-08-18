package org.heather.hardlands.module.phase;

import org.heather.hardlands.config.ConfigBuilder;
import org.heather.hardlands.config.OptionDef;
import org.heather.hardlands.core.ThreadScheduler;
import org.heather.hardlands.core.config.Option;
import org.heather.hardlands.core.config.Validator;

@ConfigBuilder(
        identifier = "timer",
        options = {
                @OptionDef(type = Integer.class, validators = Validator.Keys.NON_NEGATIVE, name = "pvpStartMinute"),
                @OptionDef(type = Integer.class, validators = Validator.Keys.NON_NEGATIVE, name = "borderStartMinute"),
                @OptionDef(type = Integer.class, validators = Validator.Keys.NON_NEGATIVE, name = "meetupStartMinute"),
                @OptionDef(type = Integer.class, validators = Validator.Keys.NON_NEGATIVE, name = "deathmatchStartMinute")
        }
)
public final class PhaseTimer extends PhaseTimerConfiguration {

    private final ThreadScheduler scheduler;

    public PhaseTimer(ThreadScheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void scheduleForOption(Runnable runnable, Option<Integer> option) {
        long ticks = option.getValue() * 20L * 60L;
        this.scheduler.scheduleSync(runnable, ticks);
    }

    @Override
    protected boolean isConfigurationValid() {
        int pvp = super.pvpStartMinute.getValue();
        int border = super.borderStartMinute.getValue();
        int meetup = super.meetupStartMinute.getValue();
        int deathmatch = super.deathmatchStartMinute.getValue();

        return pvp < border && border < meetup && meetup < deathmatch;
    }
}