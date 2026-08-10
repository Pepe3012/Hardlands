package com.hardlands.scenario;

import com.hardlands.common.option.OptionHolder;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public abstract class ScenarioModule extends OptionHolder implements Listener {

    protected void onEnable() {}
    protected void onDisable() {}

    final void enable(final Plugin plugin) {
        if (!areOptionsValid()) {
            throw new IllegalStateException("Scenario options have not been configured correctly");
        }

        Bukkit.getPluginManager().registerEvents(this, plugin);

        try {
            onEnable();
        } catch (RuntimeException exception) {
            unregisterListeners();
            throw exception;
        }
    }

    final void disable() {
        try {
            onDisable();
        } finally {
            unregisterListeners();
        }
    }

    private void unregisterListeners() {
        HandlerList.unregisterAll(this);
    }
}