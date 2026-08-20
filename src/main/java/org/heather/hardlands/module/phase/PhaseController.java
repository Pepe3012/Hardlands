package org.heather.hardlands.module.phase;

import org.heather.hardlands.Hardlands;
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
        })
public final class PhaseController extends PhaseControllerConfiguration {

    private final Hardlands plugin;
    private final ThreadScheduler scheduler;
    private Phase phase;

    public PhaseController(Hardlands plugin) {
        this.plugin = plugin;
        this.scheduler = plugin.getThreadScheduler();
    }

    public void startPhase(Phase phase) {
        this.phase = phase;
    }

    public void scheduleForOption(Runnable runnable, Option<Integer> option) {
        this.scheduler.scheduleSync(runnable, option.getValue() * 20L * 60L);
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
