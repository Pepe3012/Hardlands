package org.heather.hardlands.world.pregen;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

@Getter
@RequiredArgsConstructor
public enum PregenerationState {

    IDLE("idle", "<gray>Sin iniciar", Material.BEDROCK),
    RUNNING("running", "<yellow>En progreso", Material.DIRT),
    COMPLETED("completed", "<green>Completado", Material.GRASS_BLOCK);

    private final String key;
    private final String displayName;
    private final Material material;

    public boolean isCompleted() {
        return this == COMPLETED;
    }
}