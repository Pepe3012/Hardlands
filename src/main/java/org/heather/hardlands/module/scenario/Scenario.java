package org.heather.hardlands.module.scenario;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.heather.hardlands.Hardlands;
import org.heather.hardlands.core.config.Configuration;

public abstract class Scenario extends Configuration implements Listener {

    private Hardlands plugin;

    protected final Hardlands getPlugin() {
        if (this.plugin == null) {
            throw new IllegalStateException("Scenario has not been initialized");
        }
        return this.plugin;
    }

    final void initialize(Hardlands plugin, String identifier) {
        if (this.plugin != null) {
            throw new IllegalStateException("Scenario is already initialized");
        }

        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }

        this.setIdentifier(identifier);
        this.plugin = plugin;
    }

    final void enable() {
        if (!this.isValid()) {
            throw new IllegalStateException("Scenario configuration is invalid: " + this.getIdentifier());
        }

        Bukkit.getPluginManager().registerEvents(this, this.plugin);
    }

    final void disable() {
        HandlerList.unregisterAll(this);
    }
}
