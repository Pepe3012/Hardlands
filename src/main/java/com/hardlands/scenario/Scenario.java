package com.hardlands.scenario;

import com.hardlands.HardlandsPlugin;
import com.hardlands.util.option.Option;
import com.hardlands.util.option.Container;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class Scenario implements Listener {

    @Getter protected final Container container = new Container();

    protected final <T> Option<T> option(String key, T defaultValue) {
        return this.container.create(key, defaultValue);
    }

    protected void onEnable() {}

    protected void onDisable() {}

    void enable() {
        Bukkit.getPluginManager().registerEvents(this, HardlandsPlugin.getInstance());
        this.onEnable();
    }

    void disable() {
        HandlerList.unregisterAll(this);
        this.onDisable();
    }
}