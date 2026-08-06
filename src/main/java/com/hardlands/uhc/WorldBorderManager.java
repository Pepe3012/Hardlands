package com.hardlands.uhc;

import com.hardlands.util.option.Option;
import com.hardlands.util.option.OptionContainer;
import com.hardlands.util.option.OptionValidators;
import com.hardlands.util.TickConverter;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;

public final class WorldBorderManager {

    @Getter private final OptionContainer optionContainer = new OptionContainer();
    private final Option<World> worldOption = this.optionContainer.create("world", Bukkit.getWorld("world"));
    private final Option<Integer> centerXOption = this.optionContainer.create("center-x", 0);
    private final Option<Integer> centerZOption = this.optionContainer.create("center-z", 0);
    private final Option<Integer> survivalSizeOption = this.optionContainer.create("survival-size", 3_000, OptionValidators.Integers.POSITIVE);
    private final Option<Integer> meetupSizeOption = this.optionContainer.create("meetup-size", 300, OptionValidators.Integers.POSITIVE);
    private final Option<Integer> deathmatchSizeOption = this.optionContainer.create("deathmatch-size", 50, OptionValidators.Integers.POSITIVE);
    private final Option<Integer> meetupShrinkTimeOption = this.optionContainer.create("meetup-shrink-time", TickConverter.minutesToTicks(10), OptionValidators.Integers.NON_NEGATIVE);
    private final Option<Integer> deathmatchShrinkTimeOption = this.optionContainer.create("deathmatch-shrink-time", TickConverter.minutesToTicks(5), OptionValidators.Integers.NON_NEGATIVE);

    public void initializeForSurvival() {
        this.requireValidConfiguration();

        WorldBorder border = this.getWorldBorder();
        border.setCenter(this.centerXOption.getValue(), this.centerZOption.getValue());
        border.setSize(this.survivalSizeOption.getValue());
    }

    public int shrinkForMeetup() {
        return this.shrinkTo(this.meetupSizeOption.getValue(), this.meetupShrinkTimeOption.getValue());
    }

    public int shrinkForDeathmatch() {
        return this.shrinkTo(this.deathmatchSizeOption.getValue(), this.deathmatchShrinkTimeOption.getValue());
    }

    public boolean validate() {
        if (!this.optionContainer.validate()) return false;
        if (this.worldOption.getValue() == null) return false;

        int survivalSize = this.survivalSizeOption.getValue();
        int meetupSize = this.meetupSizeOption.getValue();
        int deathmatchSize = this.deathmatchSizeOption.getValue();

        return survivalSize >= meetupSize && meetupSize >= deathmatchSize;
    }

    private int shrinkTo(int size, int duration) {
        this.requireValidConfiguration();
        this.getWorldBorder().changeSize(size, duration);
        return duration;
    }

    private void requireValidConfiguration() {
        if (!this.validate()) {
            throw new IllegalStateException("The world border configuration is invalid. Ensure that a world is selected and that survival-size >= meetup-size >= deathmatch-size");
        }
    }

    private World getWorld() {
        World world = this.worldOption.getValue();
        if (world == null) {
            throw new IllegalStateException("The world border world has not been configured");
        }
        return world;
    }

    private WorldBorder getWorldBorder() {
        return this.getWorld().getWorldBorder();
    }

    public PregenerationRegion getPregenerationRegion() {
        World world = this.getWorld();
        WorldBorder border = world.getWorldBorder();
        return new PregenerationRegion(world.getName(), border.getCenter().getX(), border.getCenter().getZ(), border.getSize() / 2.0D);
    }

    public record PregenerationRegion(String worldName, double centerX, double centerZ, double radius) {}
}