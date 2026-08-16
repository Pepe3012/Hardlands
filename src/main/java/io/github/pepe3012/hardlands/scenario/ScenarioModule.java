package io.github.pepe3012.hardlands.scenario;

import io.github.pepe3012.hardlands.data.json.JsonConvertible;
import io.github.pepe3012.hardlands.data.option.Option;
import io.github.pepe3012.hardlands.data.option.OptionContainer;
import io.github.pepe3012.hardlands.data.option.OptionDataType;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.function.Predicate;

public abstract class ScenarioModule implements Listener, JsonConvertible {

    private final OptionContainer options;

    protected ScenarioModule(String identifier) {
        this.options = new OptionContainer(identifier);
    }

    protected final <T> Option<T> registerOption(String key, OptionDataType dataType) {
        return this.options.register(key, dataType);
    }

    protected final <T> Option<T> registerOption(String key, OptionDataType dataType, Predicate<? super T> validator) {
        return this.options.register(key, dataType, validator);
    }

    public final String getIdentifier() {
        return this.options.getIdentifier();
    }

    public final OptionContainer getOptions() {
        return this.options;
    }

    final void enable(Plugin plugin) {
        if (!this.options.validate()) {
            throw new IllegalStateException("Scenario options are not configured correctly: " + this.getIdentifier());
        }

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    final void disable() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public final String toJson() {
        return this.options.toJson();
    }

    @Override
    public final void fromJson(String json) {
        this.options.fromJson(json);
    }
}