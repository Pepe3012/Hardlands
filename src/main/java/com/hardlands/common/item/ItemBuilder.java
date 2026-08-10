package com.hardlands.common.item;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("UnstableApiUsage")
public final class ItemBuilder {

    private final ItemStack item;

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
    }

    public ItemBuilder(ItemStack item) {
        this.item = item.clone();
    }

    public ItemBuilder amount(int amount) {
        this.item.setAmount(amount);
        return this;
    }

    public ItemBuilder name(String name) {
        return this.name(MiniMessage.miniMessage().deserialize(name));
    }

    public ItemBuilder name(Component name) {
        this.item.setData(DataComponentTypes.CUSTOM_NAME, nonItalic(name));
        return this;
    }

    public ItemBuilder clearName() {
        this.item.unsetData(DataComponentTypes.CUSTOM_NAME);
        return this;
    }

    public ItemBuilder lore(String... lines) {
        return this.lore(Arrays.stream(lines).map(MiniMessage.miniMessage()::deserialize).toList());
    }

    public ItemBuilder lore(Component... lines) {
        return this.lore(List.of(lines));
    }

    public ItemBuilder lore(Collection<? extends Component> lines) {
        this.item.setData(DataComponentTypes.LORE, ItemLore.lore(lines.stream().map(ItemBuilder::nonItalic).toList()));
        return this;
    }

    public ItemBuilder addLore(String... lines) {
        return this.addLore(Arrays.stream(lines).map(MiniMessage.miniMessage()::deserialize).toList());
    }

    public ItemBuilder addLore(Component... lines) {
        return this.addLore(List.of(lines));
    }

    public ItemBuilder addLore(Collection<? extends Component> lines) {
        ItemLore current = this.item.getData(DataComponentTypes.LORE);
        List<Component> lore = current == null ? new ArrayList<>() : new ArrayList<>(current.lines());

        lines.stream().map(ItemBuilder::nonItalic).forEach(lore::add);
        this.item.setData(DataComponentTypes.LORE, ItemLore.lore(lore));
        return this;
    }

    public ItemBuilder clearLore() {
        this.item.unsetData(DataComponentTypes.LORE);
        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        this.item.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder removeEnchant(Enchantment enchantment) {
        this.item.removeEnchantment(enchantment);
        return this;
    }

    public ItemBuilder clearEnchantments() {
        this.item.removeEnchantments();
        return this;
    }

    public ItemBuilder glint(boolean glint) {
        this.item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, glint);
        return this;
    }

    public ItemBuilder clearGlintOverride() {
        this.item.unsetData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE);
        return this;
    }

    public ItemBuilder unbreakable(boolean unbreakable) {
        if (unbreakable) this.item.setData(DataComponentTypes.UNBREAKABLE);
        else this.item.unsetData(DataComponentTypes.UNBREAKABLE);
        return this;
    }

    public ItemBuilder flags(ItemFlag... flags) {
        this.item.addItemFlags(flags);
        return this;
    }

    public ItemBuilder removeFlags(ItemFlag... flags) {
        this.item.removeItemFlags(flags);
        return this;
    }

    public ItemBuilder clearFlags() {
        this.item.removeItemFlags(ItemFlag.values());
        return this;
    }

    public ItemBuilder itemModel(NamespacedKey model) {
        this.item.setData(DataComponentTypes.ITEM_MODEL, model);
        return this;
    }

    public ItemBuilder clearItemModel() {
        this.item.unsetData(DataComponentTypes.ITEM_MODEL);
        return this;
    }

    public ItemBuilder customModelData(String... values) {
        this.item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addStrings(List.of(values)));
        return this;
    }

    public ItemBuilder customModelData(Consumer<CustomModelData.Builder> editor) {
        CustomModelData.Builder builder = CustomModelData.customModelData();
        editor.accept(builder);
        this.item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, builder);
        return this;
    }

    public ItemBuilder clearCustomModelData() {
        this.item.unsetData(DataComponentTypes.CUSTOM_MODEL_DATA);
        return this;
    }

    public <P, C> ItemBuilder data(NamespacedKey key, PersistentDataType<P, C> type, C value) {
        this.item.editPersistentDataContainer(container -> container.set(key, type, value));
        return this;
    }

    public ItemBuilder removeData(NamespacedKey key) {
        this.item.editPersistentDataContainer(container -> container.remove(key));
        return this;
    }

    public <P, C> C getData(NamespacedKey key, PersistentDataType<P, C> type) {
        return this.item.getPersistentDataContainer().get(key, type);
    }

    public <P, C> boolean hasData(NamespacedKey key, PersistentDataType<P, C> type) {
        return this.item.getPersistentDataContainer().has(key, type);
    }

    public ItemBuilder editMeta(Consumer<? super ItemMeta> editor) {
        this.item.editMeta(editor);
        return this;
    }

    public <M extends ItemMeta> ItemBuilder editMeta(Class<M> type, Consumer<? super M> editor) {
        this.item.editMeta(type, editor);
        return this;
    }

    public ItemBuilder copy() {
        return new ItemBuilder(this.item);
    }

    public ItemStack build() {
        return this.item;
    }

    private static Component nonItalic(Component component) {
        return component.decoration(TextDecoration.ITALIC, false);
    }

    public ItemBuilder skullOwner(String owner) {
        return this.editMeta(SkullMeta.class, meta -> meta.setPlayerProfile(Bukkit.createProfile(owner)));
    }
}