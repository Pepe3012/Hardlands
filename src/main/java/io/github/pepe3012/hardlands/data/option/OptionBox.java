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

    private final Map<String, Option<?>> options = new LinkedHashMap<>();
    private final String key;

    public OptionBox(String key) {
        this.key = key;
    }

    public <T> Option<T> createOption(String key, OptionDataType dataType) {
        return this.registerOption(new Option<>(key, dataType));
    }

    public <T> Option<T> createOption(String key, OptionDataType dataType, Predicate<? super T> validator) {
        return this.registerOption(new Option<>(key, dataType, validator));
    }

    public <T> Option<T> createOption(String key, Class<T> valueType) {
        return this.registerOption(new Option<>(key, valueType));
    }

    public <T> Option<T> createOption(String key, Class<T> valueType, Predicate<? super T> validator) {
        return this.registerOption(new Option<>(key, valueType, validator));
    }

    public Option<Double> createOption(String key, DoublePredicate validator) {
        return this.registerOption(new Option<>(key, OptionDataType.DOUBLE, validator::test));
    }

    public Option<Integer> createOption(String key, IntPredicate validator) {
        return this.registerOption(new Option<>(key, OptionDataType.INTEGER, validator::test));
    }

    public Option<Long> createOption(String key, LongPredicate validator) {
        return this.registerOption(new Option<>(key, OptionDataType.LONG, validator::test));
    }

    public <T> Option<List<T>> createListOption(String key) {
        return this.registerOption(new Option<>(key, OptionDataType.LIST));
    }

    public <T> Option<Set<T>> createSetOption(String key) {
        return this.registerOption(new Option<>(key, OptionDataType.SET));
    }

    public <K, V> Option<Map<K, V>> createMapOption(String key) {
        return this.registerOption(new Option<>(key, OptionDataType.MAP));
    }

    public String getKey() {
        return this.key;
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

    public boolean isValid() {
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
        JsonObject values = new JsonObject();

        root.addProperty("key", this.key);

        this.options.forEach((key, option) -> {
            if (option.hasValue()) {
                values.add(key, Hardlands.GSON.toJsonTree(option.getValue()));
            }
        });

        root.add("options", values);

        return Hardlands.GSON.toJson(root);
    }

    public void deserialize(String json) {
        JsonObject root = Hardlands.GSON.fromJson(json, JsonObject.class);

        if (root == null || !root.has("options")) {
            throw new IllegalArgumentException("Invalid option box JSON");
        }

        if (root.has("key") && !this.key.equals(root.get("key").getAsString())) {
            throw new IllegalArgumentException(
                    "Expected option box '" + this.key + "' but received '" + root.get("key").getAsString() + "'"
            );
        }

        JsonObject values = root.getAsJsonObject("options");

        values.entrySet().forEach(entry -> {
            Option<?> option = this.options.get(entry.getKey());

            if (option == null) {
                return;
            }

            Object value = Hardlands.GSON.fromJson(
                    entry.getValue(),
                    option.getValueType()
            );

            option.setValue(value);
        });
    }

    private <T> Option<T> registerOption(Option<T> option) {
        if (this.options.putIfAbsent(option.getKey(), option) != null) {
            throw new IllegalArgumentException(
                    "Option already registered: " + option.getKey()
            );
        }

        return option;
    }
}