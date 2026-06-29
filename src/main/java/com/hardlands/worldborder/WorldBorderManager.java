package com.hardlands.worldborder;

import com.hardlands.util.GlobalUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class WorldBorderManager {
    private static final String DEFAULT_WORLD_NAME = "world";
    private static final double DEFAULT_SURVIVAL_DIAMETER = 2_000.0D;
    private static final double DEFAULT_MEETUP_DIAMETER = 100.0D;
    private static final double DEFAULT_DEATHMATCH_DIAMETER = 40.0D;
    private static final long DEFAULT_MEETUP_SHRINK_TICKS = GlobalUtil.minutesToTicks(10);
    private static final long DEFAULT_DEATHMATCH_SHRINK_TICKS = GlobalUtil.minutesToTicks(5);

    @Nullable private World world;

    private double survivalDiameter = DEFAULT_SURVIVAL_DIAMETER;
    private double meetupDiameter = DEFAULT_MEETUP_DIAMETER;
    private double deathmatchDiameter = DEFAULT_DEATHMATCH_DIAMETER;
    private long meetupShrinkTicks = DEFAULT_MEETUP_SHRINK_TICKS;
    private long deathmatchShrinkTicks = DEFAULT_DEATHMATCH_SHRINK_TICKS;

    public void initializeSurvivalBorder() {
        this.world = requireDefaultWorld();

        WorldBorder border = this.getWorldBorder();
        border.setCenter(0.0D, 0.0D);
        border.setSize(this.survivalDiameter);
    }

    public void startMeetupShrink() {
        this.getWorldBorder().changeSize(this.meetupDiameter, this.meetupShrinkTicks);
    }

    public void startDeathmatchShrink() {
        this.getWorldBorder().changeSize(this.deathmatchDiameter, this.deathmatchShrinkTicks);
    }

    public void setWorld(@NotNull World world) {
        this.world = Objects.requireNonNull(world, "World cannot be null.");
    }

    public void setSurvivalDiameter(double survivalDiameter) {
        this.survivalDiameter = validateDiameter(survivalDiameter, "survivalDiameter");
    }

    public void setMeetupDiameter(double meetupDiameter) {
        this.meetupDiameter = validateDiameter(meetupDiameter, "meetupDiameter");
    }

    public void setDeathmatchDiameter(double deathmatchDiameter) {
        this.deathmatchDiameter = validateDiameter(deathmatchDiameter, "deathmatchDiameter");
    }

    public void setMeetupShrinkTicks(long meetupShrinkTicks) {
        this.meetupShrinkTicks = validateTicks(meetupShrinkTicks, "meetupShrinkTicks");
    }

    public void setDeathmatchShrinkTicks(long deathmatchShrinkTicks) {
        this.deathmatchShrinkTicks = validateTicks(deathmatchShrinkTicks, "deathmatchShrinkTicks");
    }

    private WorldBorder getWorldBorder() {
        return this.getWorld().getWorldBorder();
    }

    private @NotNull World getWorld() {
        if (this.world != null) return this.world;
        return requireDefaultWorld();
    }

    private static @NotNull World requireDefaultWorld() {
        World world = Bukkit.getWorld(DEFAULT_WORLD_NAME);
        if (world == null) throw new IllegalStateException("Default world '" + DEFAULT_WORLD_NAME + "' is not loaded.");

        return world;
    }

    private static double validateDiameter(double diameter, @NotNull String name) {
        if (diameter < 1.0D) throw new IllegalArgumentException(name + " must be at least 1.0.");

        return diameter;
    }

    private static long validateTicks(long ticks, @NotNull String name) {
        if (ticks < 0L) throw new IllegalArgumentException(name + " cannot be negative.");

        return ticks;
    }
}