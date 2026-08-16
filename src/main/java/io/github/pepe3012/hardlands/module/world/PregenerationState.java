package io.github.pepe3012.hardlands.module.world;

import org.bukkit.Material;

public enum PregenerationState {

    IDLE("idle", "<gray>Sin iniciar", Material.BEDROCK),
    RUNNING("running", "<yellow>En progreso", Material.DIRT),
    PAUSED("paused", "<gold>Pausado", Material.STONE),
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
        return this.key;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Material getMaterial() {
        return this.material;
    }
}