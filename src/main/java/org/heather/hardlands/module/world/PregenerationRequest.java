package org.heather.hardlands.module.world;

import org.popcraft.chunky.api.ChunkyAPI;

import java.util.Optional;

public record PregenerationRequest(
        String worldName,
        double centerX,
        double centerZ,
        int worldSize
) {

    public Optional<PregenerationRequest> reviewAndAccept(ChunkyAPI chunky) {
        if (chunky.isRunning(this.worldName)) {
            throw new IllegalStateException("Chunky is already pregenerating " + this.worldName);
        }

        double radius = this.worldSize / 2.0D;

        boolean accepted = chunky.startTask(
                this.worldName,
                "square",
                this.centerX,
                this.centerZ,
                radius,
                radius,
                "concentric"
        );

        return accepted ? Optional.of(this) : Optional.empty();
    }
}