package io.github.pepe3012.hardlands.scenario;

import io.github.pepe3012.hardlands.data.option.OptionBox;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public abstract class ScenarioModule implements Listener {

    protected final OptionBox optionBox;

    protected ScenarioModule(String identifier) {
        this.optionBox = new OptionBox(identifier);
    }

    final void enable(Plugin plugin) {
        if (!this.optionBox.validate()) {
            throw new IllegalStateException("Scenario options have not been configured correctly");
        }

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    final void disable() {
        HandlerList.unregisterAll(this);
    }

    public OptionBox getOptionBox() {
        return this.optionBox;
    }
}