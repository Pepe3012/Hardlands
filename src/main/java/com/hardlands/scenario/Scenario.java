package com.hardlands.scenario;

import com.hardlands.Hardlands;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.*;

public abstract class Scenario implements Listener {
    private final Map<String, Option<?>> options = new LinkedHashMap<>();

    @SuppressWarnings("unchecked")
    public <T> Option<T> getOption(String key) {
        return (Option<T>) this.options.get(key);
    }

    public Collection<Option<?>> getOptions() {
        return Collections.unmodifiableCollection(this.options.values());
    }

    protected void onEnable() {}
    protected void onDisable() {}

    protected <T> Option<T> createOption(String key, T defaultValue) {
        Option<T> option = new Option<>(key, defaultValue);
        this.options.put(key, option);
        return option;
    }

    void enable() {
        Bukkit.getPluginManager().registerEvents(this, Hardlands.getInstance());
        this.onEnable();
    }

    void disable() {
        HandlerList.unregisterAll(this);
        this.onDisable();
    }

    public static class Option<T> {
        private final String key;
        private T value;

        Option(String key, T value) {
            this.key = key;
            this.value = value;
        }

        public void setValue(T value) {
            this.value = value;
        }

        public String getKey() {
            return this.key;
        }

        public T getValue() {
            return this.value;
        }
    }
}