package com.hardlands.item;

import com.hardlands.uhc.PreparationManager;
import com.hardlands.uhc.PreparationManager.PreparationState;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Function;

public enum InventoryItem {

    SCENARIOS(simple(Material.CHERRY_SAPLING, "<#cc066c>Escenarios",
            "<gray>Activa, desactiva y configura",
            "<gray>los escenarios de la partida.")),

    TEAMS(simple(Material.PLAYER_HEAD, "<#D9CE54>Equipos",
            "<gray>Crea, elimina y administra",
            "<gray>los equipos de los jugadores.")),

    WORLD_BORDER(simple(Material.STRUCTURE_VOID, "<#27DDF5>Borde del mundo",
            "<gray>Configura el tamaño inicial,",
            "<gray>el tamaño final y la reducción.")),

    SETTINGS(simple(Material.COMPARATOR, "<#D16A2E>Configuración",
            "<gray>Configura las duraciones, reglas",
            "<gray>y opciones generales de la partida.")),

    PREPARATION(InventoryItem::createPreparationItem),

    PREVIOUS(new ItemBuilder(Material.PLAYER_HEAD)
            .skullOwner("MHF_ArrowLeft")
            .name("<yellow>Anterior")
            .lore("<gray>Regresa al menú o página anterior.")
            .build()),

    NEXT(new ItemBuilder(Material.PLAYER_HEAD)
            .skullOwner("MHF_ArrowRight")
            .name("<yellow>Siguiente")
            .lore("<gray>Avanza a la siguiente página.")
            .build());

    private final Function<PreparationManager, ItemStack> factory;

    InventoryItem(ItemStack item) {
        this(preparation -> item.clone());
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

    private static ItemStack simple(Material material, String name, String... lore) {
        return new ItemBuilder(material).name(name).lore(lore).build();
    }

    private static ItemStack createPreparationItem(@Nullable PreparationManager preparation) {
        PreparationState state = preparation == null
                ? PreparationState.NOT_STARTED
                : preparation.getState();

        float progress = preparation == null
                ? 0.0F
                : preparation.getProgress();

        ItemBuilder builder = new ItemBuilder(state.getMaterial())
                .name("<#3C674A>Preparación")
                .lore(
                        "<gray>Prepara el mundo y genera",
                        "<gray>los chunks antes de iniciar.",
                        "",
                        "<gray>Estado: " + state.getColor() + state.getDisplayName(),
                        "<gray>Progreso: <white>" + String.format(Locale.ROOT, "%.1f%%", progress)
                );

        if (state == PreparationState.COMPLETED) builder.glint(true);

        return builder.build();
    }
}