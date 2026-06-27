package com.hardlands.scenario;

import com.hardlands.Hardlands;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class Scenario implements Listener {
    private final Map<String, Option<?>> options = new LinkedHashMap<>();
    @Nullable private Hardlands plugin;

    @Nullable
    public Option<?> getOption(String key) {
        return this.options.get(key);
    }

    public Collection<Option<?>> getOptions() {
        return Collections.unmodifiableCollection(this.options.values());
    }

    protected void onEnable() {}
    protected void onDisable() {}

    protected <T, O extends Option<T>> O registerOption(O option) {
        this.options.put(option.getKey(), option);
        return option;
    }

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