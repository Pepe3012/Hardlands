package io.github.pepe3012.hardlands.world;

import io.github.pepe3012.hardlands.data.json.JsonConvertible;
import io.github.pepe3012.hardlands.data.option.Option;
import io.github.pepe3012.hardlands.data.option.OptionContainer;
import io.github.pepe3012.hardlands.data.option.OptionDataType;
import io.github.pepe3012.hardlands.data.option.OptionValidators;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.popcraft.chunky.api.ChunkyAPI;
import org.popcraft.chunky.api.event.task.GenerationCompleteEvent;
import org.popcraft.chunky.api.event.task.GenerationProgressEvent;

public final class WorldManager implements JsonConvertible {

    private final OptionContainer options = new OptionContainer("world");
    private final Option<String> worldName = this.options.register("world-name", OptionDataType.STRING);
    private final Option<Double> centerX = this.options.register("center-x", OptionDataType.DOUBLE);
    private final Option<Double> centerZ = this.options.register("center-z", OptionDataType.DOUBLE);
    private final Option<Integer> survivalBorderSize = this.options.register("survival-border-size", OptionValidators.Integers.POSITIVE);
    private final Option<Integer> meetupBorderSize = this.options.register("meetup-border-size", OptionValidators.Integers.POSITIVE);
    private final Option<Integer> deathmatchBorderSize = this.options.register("deathmatch-border-size", OptionValidators.Integers.POSITIVE);
    private final Option<Integer> meetupShrinkDuration = this.options.register("meetup-shrink-duration", OptionValidators.Integers.NON_NEGATIVE);
    private final Option<Integer> deathmatchShrinkDuration = this.options.register("deathmatch-shrink-duration", OptionValidators.Integers.NON_NEGATIVE);

    private final ChunkyAPI chunky;

    private PregenerationState pregenerationState = PregenerationState.IDLE;
    private PregenerationRequest pregenerationRequest;
    private float pregenerationProgress;

    public WorldManager(ChunkyAPI chunky) {
        this.chunky = chunky;
        this.chunky.onGenerationProgress(this::handleGenerationProgress);
        this.chunky.onGenerationComplete(this::handleGenerationComplete);
    }

    public boolean isValid() {
        if (!this.options.validate()) return false;

        int survival = this.survivalBorderSize.getValue();
        int meetup = this.meetupBorderSize.getValue();
        int deathmatch = this.deathmatchBorderSize.getValue();

        return survival >= meetup && meetup >= deathmatch;
    }

    public synchronized void startPregeneration() {
        if (this.isPregenerationRunning()) {
            throw new IllegalStateException("Pregeneration is already running");
        }

        PregenerationRequest request = this.pregenerationRequest != null
                ? this.pregenerationRequest
                : this.createPregenerationRequest();

        request.reviewAndAccept(this.chunky).ifPresent(accepted -> {
            this.pregenerationRequest = accepted;
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

    public void initializeSurvivalBorder() {
        WorldBorder border = this.getWorld().getWorldBorder();

        border.setCenter(this.centerX.getValue(), this.centerZ.getValue());
        border.setSize(this.survivalBorderSize.getValue());
    }

    public void shrinkBorderForMeetup() {
        this.resizeBorder(this.meetupBorderSize.getValue(), this.meetupShrinkDuration.getValue());
    }

    public void shrinkBorderForDeathmatch() {
        this.resizeBorder(this.deathmatchBorderSize.getValue(), this.deathmatchShrinkDuration.getValue());
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

    private void resizeBorder(double size, long duration) {
        this.getWorld().getWorldBorder().changeSize(size, duration);
    }

    private synchronized void handleGenerationProgress(GenerationProgressEvent event) {
        this.pregenerationProgress = event.progress();
    }

    private synchronized void handleGenerationComplete(GenerationCompleteEvent event) {
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

    @Override
    public String toJson() {
        return this.options.toJson();
    }

    @Override
    public void fromJson(String json) {
        this.options.fromJson(json);
    }
}