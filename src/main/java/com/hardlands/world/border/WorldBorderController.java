package com.hardlands.world.border;

import com.hardlands.common.option.Option;
import com.hardlands.common.option.OptionHolder;
import com.hardlands.common.option.OptionValidators;
import org.bukkit.World;
import org.bukkit.WorldBorder;

public final class WorldBorderController extends OptionHolder {

    private final Option<World> worldOption = createOption("world", World.class);
    private final Option<Integer> centerXOption = createOption("center-x", Integer.class);
    private final Option<Integer> centerZOption = createOption("center-z", Integer.class);

    private final Option<Integer> survivalSizeOption = createOption("survival-size", Integer.class, OptionValidators.Integers.POSITIVE);
    private final Option<Integer> meetupSizeOption = createOption("meetup-size", Integer.class, OptionValidators.Integers.POSITIVE);
    private final Option<Integer> deathmatchSizeOption = createOption("deathmatch-size", Integer.class, OptionValidators.Integers.POSITIVE);

    private final Option<Integer> meetupShrinkTimeOption = createOption("meetup-shrink-time", Integer.class, OptionValidators.Integers.NON_NEGATIVE);
    private final Option<Integer> deathmatchShrinkTimeOption = createOption("deathmatch-shrink-time", Integer.class, OptionValidators.Integers.NON_NEGATIVE);

    public void initializeBorderForSurvival() {
        requireValidConfiguration();

        WorldBorder border = getConfiguredWorld().getWorldBorder();
        border.setCenter(centerXOption.getValue(), centerZOption.getValue());
        border.setSize(survivalSizeOption.getValue());
    }

    public int shrinkBorderForMeetup() {
        return shrinkBorderTo(meetupSizeOption.getValue(), meetupShrinkTimeOption.getValue());
    }

    public int shrinkBorderForDeathmatch() {
        return shrinkBorderTo(deathmatchSizeOption.getValue(), deathmatchShrinkTimeOption.getValue());
    }

    public boolean isConfigurationValid() {
        if (!areOptionsValid()) return false;

        int survivalSize = survivalSizeOption.getValue();
        int meetupSize = meetupSizeOption.getValue();
        int deathmatchSize = deathmatchSizeOption.getValue();

        return survivalSize >= meetupSize && meetupSize >= deathmatchSize;
    }

    public BorderRegion getSurvivalBorderRegion() {
        requireValidConfiguration();

        return new BorderRegion(
                getConfiguredWorld().getName(),
                centerXOption.getValue(),
                centerZOption.getValue(),
                survivalSizeOption.getValue() / 2.0D
        );
    }

    private int shrinkBorderTo(int size, int duration) {
        requireValidConfiguration();
        getConfiguredWorld().getWorldBorder().changeSize(size, duration);

        return duration;
    }

    private void requireValidConfiguration() {
        if (!isConfigurationValid()) {
            throw new IllegalStateException("The world border configuration is invalid");
        }
    }

    private World getConfiguredWorld() {
        World world = worldOption.getValue();

        if (world == null) {
            throw new IllegalStateException("The world border world has not been configured");
        }

        return world;
    }

    public record BorderRegion(String worldName, double centerX, double centerZ, double radius) {}
}