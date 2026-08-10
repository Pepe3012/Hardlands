package com.hardlands.world;

import com.hardlands.world.border.WorldBorderController;
import com.hardlands.world.chunky.PregenerationRequest;
import com.hardlands.world.chunky.WorldPregenerationController;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.popcraft.chunky.api.ChunkyAPI;

public final class WorldManager {

    @Getter private final WorldBorderController borderController;
    @Getter private final WorldPregenerationController pregenerationController;

    public WorldManager() {
        borderController = new WorldBorderController();
        pregenerationController = new WorldPregenerationController(requireChunkyApi());
    }

    public void initializeWorldBorderForSurvival() {
        borderController.initializeBorderForSurvival();
    }

    public int shrinkWorldBorderForMeetup() {
        return borderController.shrinkBorderForMeetup();
    }

    public int shrinkWorldBorderForDeathmatch() {
        return borderController.shrinkBorderForDeathmatch();
    }

    public boolean isWorldBorderConfigurationValid() {
        return borderController.isConfigurationValid();
    }

    public void startWorldPregeneration() {
        WorldBorderController.BorderRegion region = borderController.getSurvivalBorderRegion();
        PregenerationRequest request = PregenerationRequest.square(region.worldName(), region.centerX(), region.centerZ(), region.radius());

        pregenerationController.startPregeneration(request);
    }

    public void cancelWorldPregeneration() {
        pregenerationController.cancelPregeneration();
    }

    public void resetWorldPregeneration() {
        pregenerationController.resetPregeneration();
    }

    public boolean isWorldPregenerationRunning() {
        return pregenerationController.isRunning();
    }

    public boolean isWorldPregenerationCompleted() {
        return pregenerationController.isCompleted();
    }

    private static ChunkyAPI requireChunkyApi() {
        ChunkyAPI chunky = Bukkit.getServicesManager().load(ChunkyAPI.class);

        if (chunky == null) {
            throw new IllegalStateException("Chunky is not installed or its API is unavailable");
        }

        return chunky;
    }
}