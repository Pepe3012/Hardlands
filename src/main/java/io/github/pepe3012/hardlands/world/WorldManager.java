package io.github.pepe3012.hardlands.world;

import io.github.pepe3012.hardlands.data.option.Option;
import io.github.pepe3012.hardlands.data.option.OptionBox;
import io.github.pepe3012.hardlands.data.option.OptionDataType;
import io.github.pepe3012.hardlands.data.option.OptionValidators;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.popcraft.chunky.api.ChunkyAPI;
import org.popcraft.chunky.api.event.task.GenerationCompleteEvent;
import org.popcraft.chunky.api.event.task.GenerationProgressEvent;

public final class WorldManager {

    private final OptionBox box = new OptionBox("world");
    private final Option<String> worldName = this.box.place("worldManager-name", OptionDataType.STRING);
    private final Option<Double> centerX = this.box.place("center-x", OptionDataType.DOUBLE);
    private final Option<Double> centerZ = this.box.place("center-z", OptionDataType.DOUBLE);
    private final Option<Integer> survivalBorderSize = this.box.place("survival-border-size", OptionValidators.Integers.POSITIVE);
    private final Option<Integer> meetupBorderSize = this.box.place("meetup-border-size", OptionValidators.Integers.POSITIVE);
    private final Option<Integer> deathmatchBorderSize = this.box.place("deathmatch-border-size", OptionValidators.Integers.POSITIVE);
    private final Option<Integer> meetupShrinkDuration = this.box.place("meetup-shrink-duration", OptionValidators.Integers.NON_NEGATIVE);
    private final Option<Integer> deathmatchShrinkDuration = this.box.place("deathmatch-shrink-duration", OptionValidators.Integers.NON_NEGATIVE);

    private final ChunkyAPI chunky;

    private PregenerationState pregenerationState = PregenerationState.IDLE;
    private PregenerationRequest activePregeneration;
    private float pregenerationProgress;

    public WorldManager(ChunkyAPI chunky) {
        this.chunky = chunky;
        this.chunky.onGenerationProgress(this::onGenerationProgress);
        this.chunky.onGenerationComplete(this::onGenerationComplete);
    }

    public boolean validate() {
        if (!this.box.validate()) {
            return false;
        }

        int survivalSize = this.survivalBorderSize.getValue();
        int meetupSize = this.meetupBorderSize.getValue();
        int deathmatchSize = this.deathmatchBorderSize.getValue();

        return survivalSize >= meetupSize && meetupSize >= deathmatchSize;
    }

    public synchronized void startPregeneration() {
        if (this.isPregenerationRunning()) {
            throw new IllegalStateException("Pregeneration is already running");
        }

        PregenerationRequest request = this.activePregeneration != null
                ? this.activePregeneration
                : this.createPregenerationRequest();

        request.reviewAndAccept(this.chunky).ifPresent(acceptedRequest -> {
            this.activePregeneration = acceptedRequest;
            this.pregenerationState = PregenerationState.RUNNING;
        });
    }

    public synchronized void pausePregeneration() {
        if (!this.isPregenerationRunning()) {
            throw new IllegalStateException("Pregeneration is not running");
        }

        String worldName = this.worldName.getValue();

        if (!this.chunky.cancelTask(worldName)) {
            throw new IllegalStateException("Failed to pause pregeneration for " + worldName);
        }

        this.pregenerationState = PregenerationState.PAUSED;
    }

    public void shrinkBorderForSurvival() {
        WorldBorder border = this.getWorld().getWorldBorder();

        border.setCenter(this.centerX.getValue(), this.centerZ.getValue());
        border.setSize(this.survivalBorderSize.getValue());
    }

    public void shrinkBorderForMeetup() {
        this.getWorld().getWorldBorder().changeSize(
                this.meetupBorderSize.getValue(),
                this.meetupShrinkDuration.getValue()
        );
    }

    public void shrinkBorderForDeathmatch() {
        this.getWorld().getWorldBorder().changeSize(
                this.deathmatchBorderSize.getValue(),
                this.deathmatchShrinkDuration.getValue()
        );
    }

    public PregenerationState getPregenerationState() {
        return this.pregenerationState;
    }

    public float getPregenerationProgress() {
        return this.pregenerationProgress;
    }

    public synchronized boolean isPregenerationRunning() {
        return this.pregenerationState == PregenerationState.RUNNING;
    }

    public synchronized boolean isPregenerationCompleted() {
        return this.pregenerationState == PregenerationState.COMPLETED;
    }

    public String serializeOptions() {
        return this.box.serialize();
    }

    public void deserializeOptions(String json) {
        this.box.deserialize(json);
    }

    private synchronized void onGenerationProgress(GenerationProgressEvent event) {
        this.pregenerationProgress = event.progress();
    }

    private synchronized void onGenerationComplete(GenerationCompleteEvent event) {
        this.pregenerationProgress = 100.0F;
        this.pregenerationState = PregenerationState.COMPLETED;
    }

    private PregenerationRequest createPregenerationRequest() {
        return new PregenerationRequest(
                this.worldName.getValue(),
                this.centerX.getValue(),
                this.centerZ.getValue(),
                this.survivalBorderSize.getValue()
        );
    }

    private World getWorld() {
        World world = Bukkit.getWorld(this.worldName.getValue());

        if (world == null) {
            throw new IllegalStateException("World not found: " + this.worldName.getValue());
        }

        return world;
    }
}