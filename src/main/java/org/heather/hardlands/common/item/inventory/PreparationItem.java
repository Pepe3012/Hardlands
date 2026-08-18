package org.heather.hardlands.common.item.inventory;

import org.bukkit.inventory.ItemStack;
import org.heather.hardlands.common.item.ItemBuilder;
import org.heather.hardlands.module.world.PregenerationManager;

public final class PreparationItem {

    private PreparationItem() {}

    public static ItemStack build(PregenerationManager pregenerationManager) {
        PregenerationManager.State state = pregenerationManager.getPregenerationState();

        return new ItemBuilder(state.getMaterial())
                .name("Preparación")
                .lore(
                        "<gray>Prepara el mundo antes de iniciar la partida.",
                        "",
                        "<gray>Estado: " + state.getDisplayName(),
                        "<gray>Progreso: <white>%.1f%%".formatted(pregenerationManager.getPregenerationProgress())
                )
                .build();
    }
}