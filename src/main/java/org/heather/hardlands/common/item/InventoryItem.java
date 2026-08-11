package org.heather.hardlands.common.item;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.heather.hardlands.world.pregen.PregenerationController;
import org.heather.hardlands.world.pregen.PregenerationState;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Function;

@RequiredArgsConstructor
public enum InventoryItem {

    PREPARATION(InventoryItem::createPregenerationItem),
    PREVIOUS(head("MHF_ArrowLeft", "<yellow>Anterior", "Regresa al menú o página anterior.")),
    NEXT(head("MHF_ArrowRight", "<yellow>Siguiente", "Avanza a la siguiente página."));

    private final Function<@Nullable PregenerationController, ItemStack> factory;

    InventoryItem(ItemStack item) {
        this(_ -> item.clone());
    }

    public ItemStack build() {
        return this.build(null);
    }

    public ItemStack build(@Nullable PregenerationController pregeneration) {
        return this.factory.apply(pregeneration);
    }

    public static ItemStack head(String owner, String name, String lore) {
        return new ItemBuilder(Material.PLAYER_HEAD)
                .skullOwner(owner)
                .name(name)
                .lore("<gray>" + lore)
                .build();
    }

    public static InventoryDisplay display(Material material, String name, String lore) {
        return new InventoryDisplay(material, name, lore);
    }

    public static PlacedInventoryDisplay placedDisplay(Material material, String name, String lore, int row, int column) {
        return new PlacedInventoryDisplay(display(material, name, lore), row, column);
    }

    private static ItemStack createPregenerationItem(@Nullable PregenerationController pregeneration) {
        PregenerationState state = pregeneration == null ? PregenerationState.IDLE : pregeneration.getState();
        float progress = pregeneration == null ? 0.0F : pregeneration.getProgress();

        return new ItemBuilder(state.getMaterial())
                .name("<#3C674A>Preparación")
                .lore(
                        "<gray>Prepara el mundo y genera los chunks antes de iniciar.",
                        "",
                        "<gray>Estado: " + state.getDisplayName(),
                        "<gray>Progreso: <white>" + String.format(Locale.ROOT, "%.1f%%", progress)
                )
                .glint(state.isCompleted())
                .build();
    }

    public record InventoryDisplay(Material material, String name, String lore) {

        public ItemStack build() {
            return new ItemBuilder(this.material)
                    .name(this.name)
                    .lore("<gray>" + this.lore)
                    .build();
        }
    }

    public record PlacedInventoryDisplay(InventoryDisplay display, int row, int column) {

        public ItemStack build() {
            return this.display.build();
        }
    }
}