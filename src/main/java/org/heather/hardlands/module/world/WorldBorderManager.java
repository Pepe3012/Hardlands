package org.heather.hardlands.module.world;

import org.bukkit.World;
import org.heather.hardlands.config.ConfigBuilder;
import org.heather.hardlands.config.OptionDef;
import org.heather.hardlands.core.config.Validator;
import org.bukkit.WorldBorder;

@ConfigBuilder(
        identifier = "world_border",
        options = {
                @OptionDef(type = Integer.class, validators = Validator.Keys.POSITIVE, name = "sizeForSurvival"),
                @OptionDef(type = Integer.class, validators = Validator.Keys.POSITIVE, name = "sizeForMeetup"),
                @OptionDef(type = Integer.class, validators = Validator.Keys.POSITIVE, name = "sizeForDeathmatch"),
                @OptionDef(type = Integer.class, validators = Validator.Keys.NON_NEGATIVE, name = "shrinkDurationForMeetup"),
                @OptionDef(type = Integer.class, validators = Validator.Keys.NON_NEGATIVE, name = "shrinkDurationForDeathmatch"),
                @OptionDef(type = Boolean.class, name = "teleportPlayerToSurface"),
                @OptionDef(type = Boolean.class, name = "damagePlayerOutsideBorder"),
                @OptionDef(type = Double.class, name = "centerX"),
                @OptionDef(type = Double.class, name = "centerZ")
        }
)
public final class WorldBorderManager extends WorldBorderManagerConfiguration {

    private final WorldBorder worldBorder;
    private final String worldName;

    public WorldBorderManager(World world) {
        this.worldBorder = world.getWorldBorder();
        this.worldName = world.getName();
    }

    public PregenerationRequest createPregenerationRequest() {
        return new PregenerationRequest(
                this.worldName,
                super.centerX.getValue(),
                super.centerZ.getValue(),
                super.sizeForSurvival.getValue()
        );
    }

    public void configure() {
        this.worldBorder.setCenter(super.centerX.getValue(), super.centerZ.getValue());
        this.worldBorder.setSize(super.sizeForSurvival.getValue());
    }

    public void shrinkForMeetup() {
        this.worldBorder.changeSize(
                super.sizeForMeetup.getValue(),
                super.shrinkDurationForMeetup.getValue()
        );
    }

    public void shrinkForDeathmatch() {
        this.worldBorder.changeSize(
                super.sizeForDeathmatch.getValue(),
                super.shrinkDurationForDeathmatch.getValue()
        );
    }
}