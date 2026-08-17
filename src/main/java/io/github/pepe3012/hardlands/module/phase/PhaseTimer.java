package io.github.pepe3012.hardlands.module.phase;

import io.github.pepe3012.hardlands.core.ThreadScheduler;
import io.github.pepe3012.hardlands.core.config.Configuration;
import io.github.pepe3012.hardlands.core.config.Option;
import io.github.pepe3012.hardlands.core.config.OptionValidators;

public final class PhaseTimer extends Configuration {

    public final Option<Integer> pvpStartMinute = super.registerOption("pvp-start-minute", Integer.class, OptionValidators.Integers.NON_NEGATIVE);
    public final Option<Integer> borderStartMinute = super.registerOption("border-start-minute", Integer.class, OptionValidators.Integers.NON_NEGATIVE);
    public final Option<Integer> meetupStartMinute = super.registerOption("meetup-start-minute", Integer.class, OptionValidators.Integers.NON_NEGATIVE);
    public final Option<Integer> deathmatchStartMinute = super.registerOption("deathmatch-start-minute", Integer.class, OptionValidators.Integers.NON_NEGATIVE);

    private final ThreadScheduler scheduler;

    public PhaseTimer(ThreadScheduler scheduler) {
        super("timer");
        this.scheduler = scheduler;
    }

    public void scheduleForOption(Runnable runnable, Option<Integer> option) {
        var ticks = option.getValue() * 50L; //T0do sea por mantener el uso EXCLUSIVO de ticks...
        this.scheduler.scheduleSync(runnable, ticks);
    }

    @Override
    protected boolean isConfigurationValid() {
        var pvp = this.pvpStartMinute.getValue();
        var border = this.borderStartMinute.getValue();
        var meetup = this.meetupStartMinute.getValue();
        var deathmatch = this.deathmatchStartMinute.getValue();

        return pvp < border && border < meetup && meetup < deathmatch;
    }
}
