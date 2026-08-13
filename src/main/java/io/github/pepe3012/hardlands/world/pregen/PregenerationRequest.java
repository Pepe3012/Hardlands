package io.github.pepe3012.hardlands.world.pregen;

import org.popcraft.chunky.api.ChunkyAPI;

public record PregenerationRequest(String worldName, double centerX, double centerZ, double radius) {

    public void reviewAndAccept(ChunkyAPI chunky) {
        if (!chunky.startTask(this.worldName, "square", this.centerX, this.centerZ, this.radius, this.radius, "concentric")) {
            throw new IllegalStateException("Pregeneration request was rejected for " + this.worldName);
        }
    }
}