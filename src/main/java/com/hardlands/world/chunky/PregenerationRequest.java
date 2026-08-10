package com.hardlands.world.chunky;

import org.popcraft.chunky.api.ChunkyAPI;

public record PregenerationRequest(String worldName, String shape, double centerX, double centerZ, double radiusX, double radiusZ, String pattern) {

    public PregenerationRequest {
        if (worldName == null || worldName.isBlank()) {
            throw new IllegalArgumentException("World name cannot be empty");
        }

        if (radiusX <= 0.0D || radiusZ <= 0.0D) {
            throw new IllegalArgumentException("Pregeneration radius must be positive");
        }
    }

    public void startPregeneration(ChunkyAPI chunky) {
        if (!chunky.startTask(this.worldName, this.shape, this.centerX, this.centerZ, this.radiusX, this.radiusZ, this.pattern)) {
            throw new IllegalStateException("Chunky failed to start pregenerating " + this.worldName);
        }
    }

    public static PregenerationRequest square(String worldName, double centerX, double centerZ, double radius) {
        return new PregenerationRequest(worldName, "square", centerX, centerZ, radius, radius, "concentric");
    }
}