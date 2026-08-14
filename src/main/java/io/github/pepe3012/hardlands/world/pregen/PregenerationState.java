package io.github.pepe3012.hardlands.world.pregen;

import org.bukkit.Material;

public enum PregenerationState {

    IDLE("idle", "<gray>Sin iniciar", Material.BEDROCK),
    RUNNING("running", "<yellow>En progreso", Material.DIRT),
    COMPLETED("completed", "<green>Completado", Material.GRASS_BLOCK);

    private final String key;
    private final String displayName;
    private final Material material;

    PregenerationState(String key, String displayName, Material material) {
        this.key = key;
        this.displayName = displayName;
        this.material = material;
    }

    public String getKey() {
        return key;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getMaterial() {
        return material;
    }

    public boolean isCompleted() {
        return this == COMPLETED;
    }
}