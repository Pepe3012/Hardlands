package com.hardlands.scenario;

import com.hardlands.Hardlands;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Scenario implements Listener {
    @Nullable private Hardlands plugin;

    protected void onEnable() {}
    protected void onDisable() {}

    void enable() {
        if (this.plugin == null) throw new IllegalStateException("Plugin has not been injected into scenario.");
        Bukkit.getPluginManager().registerEvents(this, this.plugin);
        this.onEnable();
    }

    void disable() {
        HandlerList.unregisterAll(this);
        this.onDisable();
    }

    void setPlugin(@NotNull Hardlands plugin) {
        this.plugin = plugin;
    }
}