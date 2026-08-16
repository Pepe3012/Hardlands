package io.github.pepe3012.hardlands.module.world;

import org.popcraft.chunky.api.ChunkyAPI;

import java.util.Optional;

public record PregenerationRequest(
        String worldName,
        double centerX,
        double centerZ,
        int worldSize
) {

    public Optional<PregenerationRequest> reviewAndAccept(final ChunkyAPI chunky) {
        if (chunky.isRunning(this.worldName)) {
            throw new IllegalStateException("Chunky is already pregenerating " + this.worldName);
        }

        double radius = this.worldSize / 2.0D;
        return chunky.startTask(this.worldName, "square", this.centerX, this.centerZ, radius, radius, "concentric")
                ? Optional.of(this)
                : Optional.empty();
    }
}