package org.heather.hardlands.world;

import lombok.Getter;
import org.heather.hardlands.world.border.WorldBorderController;
import org.heather.hardlands.world.border.WorldBorderController.BorderRegion;
import org.heather.hardlands.world.pregen.PregenerationController;
import org.heather.hardlands.world.pregen.PregenerationRequest;
import org.popcraft.chunky.api.ChunkyAPI;

public final class WorldManager {

    @Getter private final PregenerationController pregenerationController;
    @Getter private final WorldBorderController borderController;

    public WorldManager(final ChunkyAPI chunkyApi) {
        this.pregenerationController = new PregenerationController(chunkyApi);
        this.borderController = new WorldBorderController();
    }

    public void initializeSurvivalBorder() {
        this.borderController.initializeSurvivalBorder();
    }

    public int shrinkBorderForMeetup() {
        return this.borderController.shrinkForMeetup();
    }

    public int shrinkBorderForDeathmatch() {
        return this.borderController.shrinkForDeathmatch();
    }

    public boolean isBorderConfigurationValid() {
        return this.borderController.isConfigurationValid();
    }

    public void startPregeneration() {
        BorderRegion region = this.borderController.getSurvivalRegion();
        this.pregenerationController.startPregeneration(new PregenerationRequest(region.worldName(), region.centerX(), region.centerZ(), region.radius()));
    }

    public void cancelPregeneration() {
        this.pregenerationController.cancelPregeneration();
    }

    public void resetPregeneration() {
        this.pregenerationController.resetPregeneration();
    }

    public boolean isPregenerationRunning() {
        return this.pregenerationController.isRunning();
    }

    public boolean isPregenerationCompleted() {
        return this.pregenerationController.isCompleted();
    }
}