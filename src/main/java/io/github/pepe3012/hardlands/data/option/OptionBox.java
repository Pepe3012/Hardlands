package io.github.pepe3012.hardlands.data.option;

import com.google.gson.JsonObject;
import io.github.pepe3012.hardlands.Hardlands;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.DoublePredicate;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

public final class OptionBox {

    private static final String PROPERTY_IDENTIFIER = "identifier";
    private static final String PROPERTY_OPTIONS = "options";

    private final Map<String, Option<?>> options = new LinkedHashMap<>();
    private final String identifier;

    public OptionBox(String identifier) {
        this.identifier = identifier;
    }

    public <T> Option<T> place(String key, OptionDataType dataType) {
        return this.registerOption(new Option<>(key, dataType));
    }

    public <T> Option<T> place(String key, OptionDataType dataType, Predicate<? super T> validator) {
        return this.registerOption(new Option<>(key, dataType, validator));
    }

    public <T> Option<T> place(String key, Class<T> valueType) {
        return this.registerOption(new Option<>(key, valueType));
    }

    public <T> Option<T> place(String key, Class<T> valueType, Predicate<? super T> validator) {
        return this.registerOption(new Option<>(key, valueType, validator));
    }

    public Option<Double> place(String key, DoublePredicate validator) {
        return this.place(key, OptionDataType.DOUBLE, validator::test);
    }

    public Option<Integer> place(String key, IntPredicate validator) {
        return this.place(key, OptionDataType.INTEGER, validator::test);
    }

    public Option<Long> place(String key, LongPredicate validator) {
        return this.place(key, OptionDataType.LONG, validator::test);
    }

    public <T> Option<List<T>> placeList(String key) {
        return this.place(key, OptionDataType.LIST);
    }

    public <T> Option<List<T>> placeList(String key, Predicate<? super List<T>> validator) {
        return this.place(key, OptionDataType.LIST, validator);
    }

    public <T> Option<Set<T>> placeSet(String key) {
        return this.place(key, OptionDataType.SET);
    }

    public <T> Option<Set<T>> placeSet(String key, Predicate<? super Set<T>> validator) {
        return this.place(key, OptionDataType.SET, validator);
    }

    public <K, V> Option<Map<K, V>> placeMap(String key) {
        return this.place(key, OptionDataType.MAP);
    }

    public <K, V> Option<Map<K, V>> placeMap(String key, Predicate<? super Map<K, V>> validator) {
        return this.place(key, OptionDataType.MAP, validator);
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public @Nullable Option<?> getOption(String key) {
        return this.options.get(key);
    }

    public Map<String, Option<?>> getOptions() {
        return Collections.unmodifiableMap(this.options);
    }

    public boolean hasOption(String key) {
        return this.options.containsKey(key);
    }

    public boolean validate() {
        return this.options.values().stream().allMatch(Option::isValid);
    }

    public void setOptionValue(String key, Object value) {
        Option<?> option = this.options.get(key);

        if (option == null) {
            throw new IllegalArgumentException("Unknown option: " + key);
        }

        option.setValue(value);
    }

    public String serialize() {
        JsonObject root = new JsonObject();

        root.addProperty(PROPERTY_IDENTIFIER, this.identifier);
        root.add(PROPERTY_OPTIONS, this.serializeOptions());

        return Hardlands.GSON.toJson(root);
    }

    public void deserialize(String json) {
        JsonObject root = Hardlands.GSON.fromJson(json, JsonObject.class);

        if (!this.identifier.equals(root.get(PROPERTY_IDENTIFIER).getAsString())) {
            throw new IllegalArgumentException("Option box identifier does not match: " + this.identifier);
        }

        this.deserializeOptions(root.getAsJsonObject(PROPERTY_OPTIONS));
    }

    private JsonObject serializeOptions() {
        JsonObject values = new JsonObject();

        this.options.values().stream()
                .filter(Option::hasValue)
                .forEach(option -> values.add(option.getKey(), Hardlands.GSON.toJsonTree(option.getValue())));

        return values;
    }

    private void deserializeOptions(JsonObject values) {
        values.entrySet().forEach(entry -> Optional.ofNullable(this.options.get(entry.getKey()))
                .ifPresent(option -> option.setValue(Hardlands.GSON.fromJson(entry.getValue(), option.getValueType()))));
    }

    private <T> Option<T> registerOption(Option<T> option) {
        if (this.options.putIfAbsent(option.getKey(), option) != null) {
            throw new IllegalArgumentException("Option already registered: " + option.getKey());
        }

        return option;
    }
}