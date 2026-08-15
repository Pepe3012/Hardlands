package io.github.pepe3012.hardlands.world;

import io.github.pepe3012.hardlands.config.option.Option;
import io.github.pepe3012.hardlands.config.option.OptionDataType;
import io.github.pepe3012.hardlands.config.option.OptionHolder;
import io.github.pepe3012.hardlands.config.option.OptionValidators;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.popcraft.chunky.api.ChunkyAPI;
import org.popcraft.chunky.api.event.task.GenerationCompleteEvent;
import org.popcraft.chunky.api.event.task.GenerationProgressEvent;

public final class WorldManager extends OptionHolder {

    private final Option<World> world = super.createOption("world", OptionDataType.CUSTOM);
    private final Option<Double> centerX = super.createOption("center-x", OptionDataType.DOUBLE);
    private final Option<Double> centerZ = super.createOption("center-z", OptionDataType.DOUBLE);
    private final Option<Integer> survivalBorderSize = super.createOption("survival-border-size", OptionValidators.Integers.POSITIVE);
    private final Option<Integer> meetupBorderSize = super.createOption("meetup-border-size", OptionValidators.Integers.POSITIVE);
    private final Option<Integer> deathmatchBorderSize = super.createOption("deathmatch-border-size", OptionValidators.Integers.POSITIVE);
    private final Option<Integer> meetupShrinkDuration = super.createOption("meetup-shrink-duration", OptionValidators.Integers.NON_NEGATIVE);
    private final Option<Integer> deathmatchShrinkDuration = super.createOption("deathmatch-shrink-duration", OptionValidators.Integers.NON_NEGATIVE);

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
        if (!super.areOptionsValid()) {
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

        String worldName = this.activePregeneration.worldName();

        if (!this.chunky.cancelTask(worldName)) {
            throw new IllegalStateException("Failed to pause pregeneration for " + worldName);
        }

        this.pregenerationState = PregenerationState.PAUSED;
    }

    private synchronized void onGenerationProgress(GenerationProgressEvent event) {
        this.pregenerationProgress = event.progress();
    }

    private synchronized void onGenerationComplete(GenerationCompleteEvent event) {
        this.pregenerationProgress = 100.0F;
        this.pregenerationState = PregenerationState.COMPLETED;
    }

    public void shrinkBorderForSurvival() {
        WorldBorder border = this.world.getValue().getWorldBorder();
        border.setCenter(this.centerX.getValue(), this.centerZ.getValue());
        border.setSize(this.survivalBorderSize.getValue());
    }

    public void shrinkBorderForMeetup() {
        this.world.getValue().getWorldBorder().changeSize(
                this.meetupBorderSize.getValue(),
                this.meetupShrinkDuration.getValue()
        );
    }

    public void shrinkBorderForDeathmatch() {
        this.world.getValue().getWorldBorder().changeSize(
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

    private PregenerationRequest createPregenerationRequest() {
        return new PregenerationRequest(
                this.world.getValue().getName(),
                this.centerX.getValue(),
                this.centerZ.getValue(),
                this.survivalBorderSize.getValue()
        );
    }
}