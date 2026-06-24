package com.hardlands.scenario;

import com.hardlands.Hardlands;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class Scenario implements Listener {
    protected abstract void onInitialize();
    protected abstract void onTerminate();

    public void initialize() {
        Bukkit.getPluginManager().registerEvents(this, Hardlands.getInstance());
        this.onInitialize();
    }

    public void terminate() {
        HandlerList.unregisterAll(this);
        this.onTerminate();
    }
}