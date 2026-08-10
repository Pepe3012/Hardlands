package com.hardlands.common.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class MenuAction {

    private static final String LEFT_CLICK_LORE_PREFIX = "<yellow>Clic izquierdo para ";
    private static final String RIGHT_CLICK_LORE_PREFIX = "<yellow>Clic derecho para ";

    private static final Predicate<ClickType> LEFT_OR_RIGHT_CLICK = clickType -> clickType.isLeftClick() || clickType.isRightClick();

    private final Predicate<ClickType> clickPredicate;
    private final BiConsumer<Player, ClickType> action;
    private final List<String> lore;

    private MenuAction(Predicate<ClickType> clickPredicate, BiConsumer<Player, ClickType> action, List<String> lore) {
        this.clickPredicate = clickPredicate;
        this.action = action;
        this.lore = List.copyOf(lore);
    }

    public static MenuAction click(String utility, Consumer<Player> action) {
        return new MenuAction(_ -> true, (player, _) -> action.accept(player), List.of("<yellow>Haz clic para " + utility + "."));
    }

    public static MenuAction left(String utility, Consumer<Player> action) {
        return new MenuAction(ClickType::isLeftClick, (player, _) -> action.accept(player), List.of(leftClickLore(utility)));
    }

    public static MenuAction right(String utility, Consumer<Player> action) {
        return new MenuAction(ClickType::isRightClick, (player, _) -> action.accept(player), List.of(rightClickLore(utility)));
    }

    public static MenuAction leftRight(String leftUtility, Consumer<Player> leftAction, String rightUtility, Consumer<Player> rightAction) {
        return new MenuAction(LEFT_OR_RIGHT_CLICK, (player, clickType) -> {
            if (clickType.isLeftClick()) leftAction.accept(player);
            else rightAction.accept(player);
        }, leftRightLore(leftUtility, rightUtility));
    }

    public static MenuAction leftRight(String leftUtility, String rightUtility, BiConsumer<Player, ClickType> action) {
        return new MenuAction(LEFT_OR_RIGHT_CLICK, action, leftRightLore(leftUtility, rightUtility));
    }

    public MenuAction appendLore(String... lines) {
        List<String> updatedLore = new ArrayList<>(this.lore.size() + lines.length);
        updatedLore.addAll(this.lore);
        Collections.addAll(updatedLore, lines);
        return new MenuAction(this.clickPredicate, this.action, updatedLore);
    }

    public boolean execute(Player player, ClickType clickType) {
        if (!this.clickPredicate.test(clickType)) return false;
        this.action.accept(player, clickType);
        return true;
    }

    public List<String> lore() {
        return this.lore;
    }

    private static List<String> leftRightLore(String leftUtility, String rightUtility) {
        return List.of(leftClickLore(leftUtility), rightClickLore(rightUtility));
    }

    private static String leftClickLore(String utility) {
        return LEFT_CLICK_LORE_PREFIX + utility + ".";
    }

    private static String rightClickLore(String utility) {
        return RIGHT_CLICK_LORE_PREFIX + utility + ".";
    }
}