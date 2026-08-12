package org.heather.hardlands.common.item;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.heather.hardlands.Hardlands;
import org.heather.hardlands.menu.MenuDefinition;
import org.heather.hardlands.world.pregen.PregenerationController;
import org.heather.hardlands.world.pregen.PregenerationState;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
public enum InventoryItem {

    GAME(menu(MenuDefinition.GAME, Material.NETHER_STAR, "Administra el estado, las fases y el desarrollo de la partida.")),
    SCENARIOS(menu(MenuDefinition.SCENARIOS, Material.CHERRY_SAPLING, "Activa, desactiva y configura los escenarios de la partida.")),
    PLAYERS(menu(MenuDefinition.PLAYERS, Material.PLAYER_HEAD, "Administra los jugadores de la partida.")),
    WORLD(menu(MenuDefinition.WORLD, "KEYKOTV", "Configura la generación y los límites del mundo.")),
    SETTINGS(menu(MenuDefinition.SETTINGS, Material.COMPARATOR, "Configura las opciones generales de la partida.")),
    TEMPLATES(menu(MenuDefinition.TEMPLATES, Material.WRITABLE_BOOK, "Administra las plantillas de configuración.")),
    VANILLA_CHANGES(menu(MenuDefinition.VANILLA_CHANGES, "M3RG1M", "Consulta y configura los cambios realizados al juego base.")),

    PREPARATION(InventoryItem::pregeneration),
    PREVIOUS(head("MHF_ArrowLeft", "<yellow>Anterior", "Regresa al menú o página anterior.")),
    NEXT(head("MHF_ArrowRight", "<yellow>Siguiente", "Avanza a la siguiente página."));

    private final Function<PregenerationController, ItemStack> factory;

    InventoryItem(ItemStack item) {
        this(_ -> item.clone());
    }

    public ItemStack build() {
        return this.build(null);
    }

    public ItemStack build(PregenerationController pregeneration) {
        return this.factory.apply(pregeneration);
    }

    public static InventoryDisplay display(Material material, String description) {
        return new InventoryDisplay(material, description);
    }

    public static Optional<MenuDefinition> findAttachedMenu(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return Optional.empty();
        }

        String identifier = item.getItemMeta()
                .getPersistentDataContainer()
                .get(attachedMenuKey(), PersistentDataType.STRING);

        if (identifier == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(MenuDefinition.valueOf(identifier));
        } catch (IllegalArgumentException _) {
            return Optional.empty();
        }
    }

    private static ItemStack menu(MenuDefinition menu, Material material, String description) {
        return menu(menu, new ItemBuilder(material), description);
    }

    private static ItemStack menu(MenuDefinition menu, String owner, String description) {
        return menu(menu, new ItemBuilder(Material.PLAYER_HEAD).skullOwner(owner), description);
    }

    private static ItemStack menu(MenuDefinition menu, ItemBuilder builder, String description) {
        return builder
                .name(menu.getTitle())
                .lore("<gray>" + description)
                .setId(attachedMenuKey(), PersistentDataType.STRING, menu.name())
                .build();
    }

    private static ItemStack head(String owner, String name, String description) {
        return new ItemBuilder(Material.PLAYER_HEAD)
                .skullOwner(owner)
                .name(name)
                .lore("<gray>" + description)
                .build();
    }

    private static ItemStack pregeneration(PregenerationController controller) {
        PregenerationState state = controller == null ? PregenerationState.IDLE : controller.getState();
        float progress = controller == null ? 0.0F : controller.getProgress();

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

    private static NamespacedKey attachedMenuKey() {
        return Hardlands.getInstance().namespacedKey("attached_menu");
    }

    public record InventoryDisplay(Material material, String description) {

        public ItemStack build(String name) {
            return this.builder().name(name).build();
        }

        public ItemStack build(Component name) {
            return this.builder().name(name).build();
        }

        private ItemBuilder builder() {
            return new ItemBuilder(this.material)
                    .lore("<gray>" + this.description);
        }
    }
}