package io.github.pepe3012.hardlands.scenario;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import io.github.pepe3012.hardlands.config.option.OptionHolder;

public abstract class ScenarioModule extends OptionHolder implements Listener {

    final void enable(final Plugin plugin) {
        if (!super.areOptionsValid()) {
            throw new IllegalStateException("Scenario options have not been configured correctly");
        }

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    final void disable() {
        HandlerList.unregisterAll(this);
    }
}