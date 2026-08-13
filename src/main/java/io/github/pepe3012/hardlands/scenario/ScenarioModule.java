package io.github.pepe3012.hardlands.scenario;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import io.github.pepe3012.hardlands.config.option.OptionHolder;

public abstract class ScenarioModule extends OptionHolder implements Listener {

    protected void onEnable() {}

    protected void onDisable() {}

    final void enable(Plugin plugin) {
        if (!super.areOptionsValid()) {
            throw new IllegalStateException("Scenario options have not been configured correctly");
        }

        Bukkit.getPluginManager().registerEvents(this, plugin);

        try {
            this.onEnable();
        } catch (RuntimeException exception) {
            this.unregisterListeners();
            throw exception;
        }
    }

    final void disable() {
        try {
            this.onDisable();
        } finally {
            this.unregisterListeners();
        }
    }

    private void unregisterListeners() {
        HandlerList.unregisterAll(this);
    }
}