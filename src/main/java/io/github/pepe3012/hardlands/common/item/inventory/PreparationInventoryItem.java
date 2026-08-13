package io.github.pepe3012.hardlands.common.item.inventory;

import org.bukkit.inventory.ItemStack;
import io.github.pepe3012.hardlands.Hardlands;
import io.github.pepe3012.hardlands.common.item.ItemBuilder;
import io.github.pepe3012.hardlands.world.WorldManager;
import io.github.pepe3012.hardlands.world.pregen.PregenerationController;
import io.github.pepe3012.hardlands.world.pregen.PregenerationState;

import java.util.Locale;

final class PreparationInventoryItem {

    private static final int PROGRESS_BAR_LENGTH = 20;
    private static final float MAX_PROGRESS = 100.0F;

    private static final String TITLE = "<white>Pregeneración";
    private static final String LABEL = "<gray>";
    private static final String VALUE = "<white>";
    private static final String ACTION = "<yellow>Clic derecho para %s.";

    private static final String PROGRESS_FILLED = "<green>■";
    private static final String PROGRESS_EMPTY = "<dark_gray>■";

    private PreparationInventoryItem() {}

    static ItemStack build() {
        PregenerationController pregeneration = pregeneration();
        PregenerationState state = pregeneration.getState();
        float progress = normalizeProgress(pregeneration.getProgress());

        ItemBuilder builder = new ItemBuilder(state.getMaterial())
                .name(TITLE)
                .lore(
                        LABEL + "Estado: " + stateColor(state) + state.getDisplayName(),
                        LABEL + "Progreso: " + VALUE + formatProgress(progress),
                        progressBar(progress)
                )
                .glint(state.isCompleted());

        appendAction(builder, state);

        return builder.build();
    }

    static void toggle() {
        PregenerationState state = pregeneration().getState();

        switch (state) {
            case IDLE -> worldManager().startPregeneration();
            case RUNNING -> worldManager().cancelPregeneration();
            case COMPLETED -> {
                // No action available once pregeneration is complete.
            }
        }
    }

    private static void appendAction(ItemBuilder builder, PregenerationState state) {
        switch (state) {
            case IDLE -> builder.lore("", ACTION.formatted("iniciar"));
            case RUNNING -> builder.lore("", ACTION.formatted("cancelar"));
            case COMPLETED -> {
                // Completed pregeneration has no available action.
            }
        }
    }

    private static String stateColor(PregenerationState state) {
        return switch (state) {
            case IDLE -> "<yellow>";
            case RUNNING -> "<aqua>";
            case COMPLETED -> "<green>";
        };
    }

    private static String progressBar(float progress) {
        int filled = Math.round(progress / MAX_PROGRESS * PROGRESS_BAR_LENGTH);
        int empty = PROGRESS_BAR_LENGTH - filled;

        return PROGRESS_FILLED.repeat(filled) + PROGRESS_EMPTY.repeat(empty);
    }

    private static String formatProgress(float progress) {
        return String.format(Locale.ROOT, "%.1f%%", progress);
    }

    private static float normalizeProgress(float progress) {
        return Math.clamp(progress, 0.0F, MAX_PROGRESS);
    }

    private static PregenerationController pregeneration() {
        return worldManager().getPregenerationController();
    }

    private static WorldManager worldManager() {
        return Hardlands.getInstance().getWorldManager();
    }
}