package com.hardlands.game;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record GameConfig(@NotNull World world, double initialBorderDiameter, double meetupBorderDiameter, long borderShrinkDurationSeconds) {

    public GameConfig {
        Objects.requireNonNull(world, "world");

        if (initialBorderDiameter <= 0.0) {
            throw new IllegalArgumentException("Initial border diameter must be greater than 0.");
        }

        if (meetupBorderDiameter <= 0.0) {
            throw new IllegalArgumentException("Meetup border diameter must be greater than 0.");
        }

        if (meetupBorderDiameter > initialBorderDiameter) {
            throw new IllegalArgumentException("Meetup border diameter cannot be larger than the initial border diameter.");
        }

        if (borderShrinkDurationSeconds < 0L) {
            throw new IllegalArgumentException("Border shrink duration cannot be negative.");
        }
    }
}