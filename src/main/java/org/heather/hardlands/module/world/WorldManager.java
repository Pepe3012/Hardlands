package org.heather.hardlands.module.world;

import java.util.Set;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.heather.hardlands.config.ConfigBuilder;
import org.heather.hardlands.config.OptionDef;
import org.heather.hardlands.core.config.Option;
import org.heather.hardlands.core.config.Validator;
import org.popcraft.chunky.api.ChunkyAPI;

@ConfigBuilder(
        identifier = "world",
        options = {
                @OptionDef(type = Set.class, elementType = String.class, name = "enabledWorlds"),
                @OptionDef(type = Integer.class, validators = Validator.Keys.POSITIVE, name = "survivalSize"),
                @OptionDef(type = Integer.class, validators = Validator.Keys.POSITIVE, name = "meetupSize"),
                @OptionDef(type = Integer.class, validators = Validator.Keys.POSITIVE, name = "deathmatchSize"),
                @OptionDef(type = Integer.class, validators = Validator.Keys.NON_NEGATIVE, name = "meetupShrinkTime"),
                @OptionDef(type = Integer.class, validators = Validator.Keys.NON_NEGATIVE, name = "deathmatchShrinkTime"),
                @OptionDef(type = Boolean.class, name = "surfaceTeleport"),
                @OptionDef(type = Boolean.class, name = "borderDamage"),
                @OptionDef(type = Double.class, name = "centerX"),
                @OptionDef(type = Double.class, name = "centerZ")
        })
public final class WorldManager extends WorldManagerConfiguration {

    private final PregenerationManager pregenerationManager = new PregenerationManager(requireChunkyService());

    public void configure() {
        this.forEachEnabledWorld((world, centerX, centerZ, survivalSize) -> {
            WorldBorder worldBorder = world.getWorldBorder();

            worldBorder.setCenter(centerX, centerZ);
            worldBorder.setSize(survivalSize);
        });
    }

    public void pregenerate() {
        this.forEachEnabledWorld((world, centerX, centerZ, survivalSize) ->
                this.pregenerationManager.reviewAndAccept(
                        new PregenerationRequest(world.getName(), centerX, centerZ, survivalSize)));
    }

    public void shrinkForMeetup() {
        this.shrinkWorldBorders(super.meetupSize, super.meetupShrinkTime);
    }

    public void shrinkForDeathmatch() {
        this.shrinkWorldBorders(super.deathmatchSize, super.deathmatchShrinkTime);
    }

    public PregenerationManager getPregenerationManager() {
        return this.pregenerationManager;
    }

    // Utility methods
    private double scaleForDimension(World world, double value) {
        return world.getEnvironment() == World.Environment.NETHER
                ? value * (1.0 / 8.0)
                : value;
    }

    private void shrinkWorldBorders(Option<Integer> targetSizeOption, Option<Integer> shrinkDurationMinutesOption) {
        int targetSize = targetSizeOption.getValue();
        long shrinkDurationTicks = shrinkDurationMinutesOption.getValue() * 1200L;

        this.forEachEnabledWorld(world -> world.getWorldBorder().changeSize(
                this.scaleForDimension(world, targetSize),
                shrinkDurationTicks));
    }

    private void forEachEnabledWorld(Consumer<World> action) {
        super.enabledWorlds.getValue().forEach(worldName -> {
            World world = Bukkit.getWorld(worldName);

            if (world == null) {
                throw new IllegalStateException("Enabled world is not loaded: " + worldName);
            }

            action.accept(world);
        });
    }

    private void forEachEnabledWorld(WorldConfigurationConsumer action) {
        double centerX = super.centerX.getValue();
        double centerZ = super.centerZ.getValue();
        double survivalSize = super.survivalSize.getValue();

        this.forEachEnabledWorld(world -> action.accept(
                world,
                centerX,
                centerZ,
                this.scaleForDimension(world, survivalSize)
        ));
    }

    @FunctionalInterface
    private interface WorldConfigurationConsumer {
        void accept(World world, double centerX, double centerZ, double survivalSize);
    }

    private static ChunkyAPI requireChunkyService() {
        ChunkyAPI chunky = Bukkit.getServicesManager().load(ChunkyAPI.class);

        if (chunky == null) {
            throw new IllegalStateException("This plugin requires Chunky");
        }

        return chunky;
    }
}
