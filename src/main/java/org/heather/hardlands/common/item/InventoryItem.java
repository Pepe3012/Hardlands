package org.heather.hardlands.common.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.heather.hardlands.Hardlands;
import org.heather.hardlands.core.config.Option;
import org.heather.hardlands.module.world.PregenerationManager;
import org.heather.hardlands.module.world.WorldManager;
import org.heather.hardlands.util.text.TextFormatter;

import java.util.Optional;

public enum InventoryItem {

    PREVIOUS("MHF_ArrowLeft", "Anterior", "Regresa al menú o página anterior."),
    NEXT("MHF_ArrowRight", "Siguiente", "Avanza a la siguiente página."),

    SCENARIOS(Material.CHERRY_SAPLING, "Escenarios", "Activa, desactiva y configura los escenarios de la partida."),
    PLAYERS(Material.PLAYER_HEAD, "Jugadores", "Administra los jugadores de la partida."),
    GENERAL(Material.COMPARATOR, "General", "Configura las opciones generales de la partida."),
    PHASES(Material.CLOCK, "Fases", "Configura la progresión y los tiempos de las fases de la partida."),
    WORLD("KEYKOTV", "Mundo", "Configura la generación y los límites del mundo."),
    PRESETS(Material.WRITABLE_BOOK, "Plantillas", "Administra las plantillas de configuración.");

    private static final String DESCRIPTION_PREFIX = "<gray>";
    private final ItemStack item;

    InventoryItem(ItemStack item) {
        this.item = item;
    }

    InventoryItem(Material material, String name, String description) {
        this(new ItemBuilder(material)
                .name(name)
                .lore(DESCRIPTION_PREFIX + description)
                .build());
    }

    InventoryItem(String skullOwner, String name, String description) {
        this(new ItemBuilder(Material.PLAYER_HEAD)
                .skullOwner(skullOwner)
                .name(name)
                .lore(DESCRIPTION_PREFIX + description)
                .build());
    }

    public static ItemStack createPreparationItem() {
        WorldManager worldManager = Hardlands.getInstance().getWorldManagerOrThrow();
        PregenerationManager pregenerationManager = worldManager.getPregenerationManager();

        Option<Integer> survivalSizeOption = worldManager.getSurvivalSizeOption();
        Integer borderSize = survivalSizeOption.getValue();

        PregenerationManager.State state = pregenerationManager.getState();
        float progress = pregenerationManager.getProgress();

        String borderSizeText = survivalSizeOption.isValid() && borderSize != null
                ? "{%1$d × %1$d}".formatted(borderSize)
                : "<gray>Inválido";
        String progressText = progress >= 100.0F
                ? "{%.1f%%}".formatted(progress)
                : "<gray>%.1f%%".formatted(progress);

        return new ItemBuilder(state.getMaterial())
                .name(TextFormatter.formatTinyCaps("Preparación"))
                .formattedLore(
                        "Establece los {World Borders} según lo configurado e inicia la {pregeneración} de los mundos abiertos.",
                        "",
                        "World Border: %s".formatted(borderSizeText),
                        "Progreso: %s".formatted(progressText))
                .addLore(Component.text("Estado: ", NamedTextColor.WHITE).append(state.display()))
                .build();
    }

    public ItemStack buildItem() {
        return new ItemBuilder(this.item.clone()).setId(this.name()).build();
    }

    public static DisplayItem createDisplayItem(Material material, String description) {
        return new DisplayItem(material, description);
    }

    public static Optional<InventoryItem> findByItem(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return Optional.empty();
        }

        Optional<String> id = new ItemBuilder(item).findId();

        if (id.isEmpty()) {
            return Optional.empty();
        }

        for (InventoryItem inventoryItem : values()) {
            if (inventoryItem.name().equals(id.get())) {
                return Optional.of(inventoryItem);
            }
        }

        return Optional.empty();
    }

    public record DisplayItem(Material material, String description) {

        public ItemStack buildItem(String name) {
            return new ItemBuilder(this.material)
                    .name(name)
                    .lore(DESCRIPTION_PREFIX + this.description)
                    .build();
        }
    }
}