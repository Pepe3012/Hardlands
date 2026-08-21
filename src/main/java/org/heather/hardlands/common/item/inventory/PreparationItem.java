package org.heather.hardlands.common.item.inventory;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;
import org.heather.hardlands.common.item.ItemBuilder;
import org.heather.hardlands.core.config.Option;
import org.heather.hardlands.module.world.PregenerationManager;
import org.heather.hardlands.module.world.WorldManager;
import org.heather.hardlands.util.text.TextFormatter;

public final class PreparationItem {

    private PreparationItem() {}

    public static ItemStack build(WorldManager worldManager) {
        PregenerationManager pregenerationManager = worldManager.getPregenerationManager();
        PregenerationManager.State state = pregenerationManager.getState();

        Option<Integer> survivalSizeOption = worldManager.getSurvivalSizeOption();
        Integer borderSize = survivalSizeOption.getValue();

        String borderSizeText = survivalSizeOption.isValid() && borderSize != null
                ? "%1$d × %1$d".formatted(borderSize)
                : "Inválido";

        return new ItemBuilder(state.getMaterial())
                .name(TextFormatter.formatTinyCaps("Preparación"))
                .formattedLore("Establece los {World Borders} según lo configurado e inicia la <pregeneración> de los mundos abiertos.",
                        "",
                        "World Border: {%s}".formatted(borderSizeText),
                        "Progreso: {%.1f%%}".formatted(pregenerationManager.getProgress()))
                .addLore(Component.text("Estado: ", NamedTextColor.WHITE).append(state.display()))
                .build();
    }
}
