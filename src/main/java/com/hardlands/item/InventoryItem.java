package com.hardlands.item;

import com.hardlands.uhc.PreparationManager;
import com.hardlands.uhc.PreparationManager.PreparationState;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Function;

public enum InventoryItem {

    SCENARIOS(item(Material.CHERRY_SAPLING, "#cc066c", "Escenarios", "Activa, desactiva y configura", "los escenarios de la partida.")),
    PLAYERS(item(Material.NAME_TAG, "#D9CE54", "Jugadores", "Administra jugadores, equipos,", "estados y participación.")),
    WORLD_BORDER(item(Material.STRUCTURE_VOID, "#27DDF5", "Borde del mundo", "Configura el tamaño inicial,", "final y su reducción.")),
    SETTINGS(item(Material.COMPARATOR, "#D16A2E", "Configuración", "Configura duraciones, reglas", "y opciones generales.")),
    TEMPLATES(item(Material.WRITABLE_BOOK, "#A78BFA", "Plantillas", "Guarda, carga y administra", "configuraciones de UHC.")),
    VANILLA_CHANGES(item(Material.REPEATER, "#E0A84B", "Cambios de Vanilla", "Modifica mecánicas vanilla", "para adaptarlas al UHC.")),

    PREPARATION(InventoryItem::createPreparationItem),

    PREVIOUS(head("MHF_ArrowLeft", "Anterior", "Regresa al menú o página anterior.")),
    NEXT(head("MHF_ArrowRight", "Siguiente", "Avanza a la siguiente página."));

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

    private static ItemStack item(Material material, String color, String name, String... lore) {
        for (int i = 0; i < lore.length; i++) {
            if (!lore[i].isEmpty()) {
                lore[i] = "<gray>" + lore[i];
            }
        }

        return new ItemBuilder(material)
                .name("<" + color + ">" + name)
                .lore(lore)
                .build();
    }

    private static ItemStack head(String owner, String name, String lore) {
        return new ItemBuilder(Material.PLAYER_HEAD)
                .skullOwner(owner)
                .name("<yellow>" + name)
                .lore("<gray>" + lore)
                .build();
    }

    private static ItemStack createPreparationItem(@Nullable PreparationManager preparation) {
        PreparationState state = preparation == null ? PreparationState.NOT_STARTED : preparation.getState();
        float progress = preparation == null ? 0.0F : preparation.getProgress();

        return new ItemBuilder(state.getMaterial())
                .name("<#3C674A>Preparación")
                .lore(
                        "<gray>Prepara el mundo y genera",
                        "<gray>los chunks antes de iniciar.",
                        "",
                        "<gray>Estado: " + state.getColor() + state.getDisplayName(),
                        "<gray>Progreso: <white>" + String.format(Locale.ROOT, "%.1f%%", progress)
                )
                .glint(state == PreparationState.COMPLETED)
                .build();
    }
}