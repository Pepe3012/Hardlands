package com.hardlands.scenario;

import com.hardlands.HardlandsPlugin;
import com.hardlands.option.OptionContainer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class Scenario implements Listener {

    @Getter protected final OptionContainer optionContainer = new OptionContainer();

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    void enable() {
        Bukkit.getPluginManager().registerEvents(this, HardlandsPlugin.INSTANCE);
        this.onEnable();
    }

    void disable() {
        HandlerList.unregisterAll(this);
        this.onDisable();
    }
}