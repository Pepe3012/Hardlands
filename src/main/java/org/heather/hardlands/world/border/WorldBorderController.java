package org.heather.hardlands.world.border;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.heather.hardlands.config.option.Option;
import org.heather.hardlands.config.option.OptionDataType;
import org.heather.hardlands.config.option.OptionHolder;
import org.heather.hardlands.config.option.OptionValidators;

public final class WorldBorderController extends OptionHolder {

    private final Option<String> worldNameOption = super.createOption("world", OptionDataType.STRING);
    private final Option<Integer> centerXOption = super.createOption("center-x", OptionDataType.INTEGER);
    private final Option<Integer> centerZOption = super.createOption("center-z", OptionDataType.INTEGER);

    private final Option<Integer> survivalSizeOption = super.createOption("survival-inventorySize", OptionValidators.Integers.POSITIVE);
    private final Option<Integer> meetupSizeOption = super.createOption("meetup-inventorySize", OptionValidators.Integers.POSITIVE);
    private final Option<Integer> deathmatchSizeOption = super.createOption("deathmatch-inventorySize", OptionValidators.Integers.POSITIVE);

    private final Option<Integer> meetupShrinkDurationOption = super.createOption("meetup-shrink-duration", OptionValidators.Integers.NON_NEGATIVE);
    private final Option<Integer> deathmatchShrinkDurationOption = super.createOption("deathmatch-shrink-duration", OptionValidators.Integers.NON_NEGATIVE);

    public void initializeSurvivalBorder() {
        this.requireValidConfiguration();

        WorldBorder border = this.getWorld().getWorldBorder();
        border.setCenter(this.centerXOption.getValue(), this.centerZOption.getValue());
        border.setSize(this.survivalSizeOption.getValue());
    }

    public int shrinkForMeetup() {
        return this.shrinkTo(this.meetupSizeOption.getValue(), this.meetupShrinkDurationOption.getValue());
    }

    public int shrinkForDeathmatch() {
        return this.shrinkTo(this.deathmatchSizeOption.getValue(), this.deathmatchShrinkDurationOption.getValue());
    }

    public boolean isConfigurationValid() {
        if (!super.areOptionsValid()) return false;
        if (Bukkit.getWorld(this.worldNameOption.getValue()) == null) return false;

        int survivalSize = this.survivalSizeOption.getValue();
        int meetupSize = this.meetupSizeOption.getValue();
        int deathmatchSize = this.deathmatchSizeOption.getValue();

        return survivalSize >= meetupSize && meetupSize >= deathmatchSize;
    }

    public BorderRegion getSurvivalRegion() {
        this.requireValidConfiguration();

        return new BorderRegion(this.worldNameOption.getValue(), this.centerXOption.getValue(), this.centerZOption.getValue(), this.survivalSizeOption.getValue() / 2.0D);
    }

    private int shrinkTo(int size, int duration) {
        this.requireValidConfiguration();
        this.getWorld().getWorldBorder().changeSize(size, duration);

        return duration;
    }

    private void requireValidConfiguration() {
        if (!this.isConfigurationValid()) {
            throw new IllegalStateException("World border configuration is invalid");
        }
    }

    private World getWorld() {
        return Bukkit.getWorld(this.worldNameOption.getValue());
    }

    public record BorderRegion(String worldName, double centerX, double centerZ, double radius) {}
}