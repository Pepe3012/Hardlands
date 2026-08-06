package com.hardlands.item;

import com.hardlands.uhc.PreparationManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Function;

public enum InventoryItem {

    SCENARIOS(new ItemBuilder(Material.CHERRY_SAPLING).name("<#cc066c>Escenarios").lore("<gray>Activa, desactiva y configura", "<gray>los escenarios de la partida.", "", "<yellow>Haz clic para administrar.").build()),
    TEAMS(new ItemBuilder(Material.PLAYER_HEAD).name("<#D9CE54>Equipos").lore("<gray>Crea, elimina y administra", "<gray>los equipos de los jugadores.", "", "<yellow>Haz clic para administrar.").build()),
    WORLD_BORDER(new ItemBuilder(Material.STRUCTURE_VOID).name("<#27DDF5>Borde del mundo").lore("<gray>Configura el tamaño inicial,", "<gray>el tamaño final y la reducción.", "", "<yellow>Haz clic para configurar.").build()),
    SETTINGS(new ItemBuilder(Material.COMPARATOR).name("<#D16A2E>Configuración").lore("<gray>Configura las duraciones, reglas", "<gray>y opciones generales de la partida.", "", "<yellow>Haz clic para configurar.").build()),

    PREPARATION(InventoryItem::createPreparationItem),

    PREVIOUS(new ItemBuilder(Material.PLAYER_HEAD).skullOwner("MHF_ArrowLeft").name("<yellow>Anterior").lore("<gray>Regresa al menú o página anterior.").build()),
    NEXT(new ItemBuilder(Material.PLAYER_HEAD).skullOwner("MHF_ArrowRight").name("<yellow>Siguiente").lore("<gray>Avanza a la siguiente página.").build());

    private final Function<PreparationManager, ItemStack> factory;

    InventoryItem(ItemStack item) {
        this(_ -> item.clone());
    }

    InventoryItem(Function<PreparationManager, ItemStack> factory) {
        this.factory = factory;
    }

    public ItemStack getItem() {
        return this.getItem(null);
    }

    public ItemStack getItem(@Nullable PreparationManager preparation) {
        return this.factory.apply(preparation);
    }

    private static ItemStack createPreparationItem(@Nullable PreparationManager preparation) {
        PreparationManager.PreparationState state = preparation == null ? PreparationManager.PreparationState.NOT_STARTED : preparation.getState();
        float progress = preparation == null ? 0.0F : preparation.getProgress();

        Material material = switch (state) {
            case NOT_STARTED -> Material.BEDROCK;
            case IN_PROGRESS -> Material.DIRT;
            case COMPLETED -> Material.GRASS_BLOCK;
        };

        String stateColor = switch (state) {
            case NOT_STARTED -> "<red>";
            case IN_PROGRESS -> "<yellow>";
            case COMPLETED -> "<green>";
        };

        String description = switch (state) {
            case NOT_STARTED -> "<yellow>Haz clic para iniciar la preparación.";
            case IN_PROGRESS -> "<yellow>La pregeneración está en curso.";
            case COMPLETED -> "<green>El mundo está listo para iniciar.";
        };

        ItemBuilder builder = new ItemBuilder(material)
                .name("<#3C674A>Preparación")
                .lore(
                        "<gray>Prepara el mundo y genera",
                        "<gray>los chunks antes de iniciar.",
                        "",
                        "<gray>Estado: " + stateColor + state.getDisplayName(),
                        "<gray>Progreso: <white>" + String.format(Locale.ROOT, "%.1f%%", progress),
                        "",
                        description
                );

        if (state == PreparationManager.PreparationState.COMPLETED) builder.glint(true);

        return builder.build();
    }
}