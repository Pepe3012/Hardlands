package org.heather.hardlands.module.world;

import org.bukkit.Bukkit;
import org.heather.hardlands.config.ConfigBuilder;
import org.heather.hardlands.config.OptionDef;
import org.popcraft.chunky.api.ChunkyAPI;

import java.util.HashMap;
import java.util.Map;

@ConfigBuilder(
        identifier = "world",
        options = @OptionDef(type = String[].class, name = "accessibleWorlds")
)
public final class WorldManager extends WorldManagerConfiguration {

    private final Map<String, WorldBorderManager> worldBorderManagers = new HashMap<>();
    private final PregenerationManager pregenerationManager = new PregenerationManager(requireChunkyService());

    public PregenerationManager getPregenerationManager() {
        return this.pregenerationManager;
    }

    private static ChunkyAPI requireChunkyService() {
        ChunkyAPI chunky = Bukkit.getServicesManager().load(ChunkyAPI.class);

        if (chunky == null) {
            throw new NullPointerException("This plugin requires Chunky installed");
        }

        return chunky;
    }
}