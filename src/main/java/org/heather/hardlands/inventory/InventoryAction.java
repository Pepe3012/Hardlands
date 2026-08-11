package org.heather.hardlands.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class InventoryAction {

    private static final String CLICK_LORE = "<yellow>Haz clic para %s.";
    private static final String LEFT_CLICK_LORE = "<yellow>Clic izquierdo para %s.";
    private static final String RIGHT_CLICK_LORE = "<yellow>Clic derecho para %s.";

    private static final Predicate<ClickType> ANY_CLICK = click -> click.isLeftClick() || click.isRightClick();
    private static final Predicate<ClickType> LEFT_CLICK = ClickType::isLeftClick;
    private static final Predicate<ClickType> RIGHT_CLICK = ClickType::isRightClick;

    private final Predicate<ClickType> clickPredicate;
    private final BiConsumer<Player, ClickType> action;
    private final List<String> lore;

    private InventoryAction(Predicate<ClickType> clickPredicate, BiConsumer<Player, ClickType> action, List<String> lore) {
        this.clickPredicate = clickPredicate;
        this.action = action;
        this.lore = List.copyOf(lore);
    }

    public static InventoryAction click(String utility, Consumer<Player> action) {
        return create(ANY_CLICK, action, CLICK_LORE.formatted(utility));
    }

    public static InventoryAction left(String utility, Consumer<Player> action) {
        return create(LEFT_CLICK, action, LEFT_CLICK_LORE.formatted(utility));
    }

    public static InventoryAction right(String utility, Consumer<Player> action) {
        return create(RIGHT_CLICK, action, RIGHT_CLICK_LORE.formatted(utility));
    }

    public InventoryAction appendLore(String... lines) {
        List<String> updatedLore = new ArrayList<>(this.lore.size() + lines.length);
        updatedLore.addAll(this.lore);
        updatedLore.addAll(List.of(lines));

        return new InventoryAction(this.clickPredicate, this.action, updatedLore);
    }

    public boolean execute(Player player, ClickType click) {
        if (!this.clickPredicate.test(click)) return false;

        this.action.accept(player, click);
        return true;
    }

    public List<String> lore() {
        return this.lore;
    }

    private static InventoryAction create(Predicate<ClickType> predicate, Consumer<Player> action, String lore) {
        return new InventoryAction(predicate, (player, _) -> action.accept(player), List.of(lore));
    }
}