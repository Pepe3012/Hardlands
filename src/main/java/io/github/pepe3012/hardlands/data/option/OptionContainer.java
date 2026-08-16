package io.github.pepe3012.hardlands.data.option;

import com.google.gson.JsonObject;
import io.github.pepe3012.hardlands.Hardlands;
import io.github.pepe3012.hardlands.data.json.JsonConvertible;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.DoublePredicate;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

public final class OptionContainer implements JsonConvertible {

    private static final String IDENTIFIER_PROPERTY = "identifier";
    private static final String OPTIONS_PROPERTY = "options";

    private final Map<String, Option<?>> options = new LinkedHashMap<>();
    private final String identifier;

    public OptionContainer(String identifier) {
        this.identifier = identifier;
    }

    public boolean validate() {
        return this.options.values().stream().allMatch(Option::isValid);
    }

    public Map<String, Option<?>> getOptions() {
        return Collections.unmodifiableMap(this.options);
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public @Nullable Option<?> getOption(String key) {
        return this.options.get(key);
    }

    public boolean hasOption(String key) {
        return this.options.containsKey(key);
    }

    public <T> Option<T> register(String key, OptionDataType dataType) {
        return this.register(new Option<>(key, dataType));
    }

    public <T> Option<T> register(String key, OptionDataType dataType, Predicate<? super T> validator) {
        return this.register(new Option<>(key, dataType, validator));
    }

    public <T> Option<T> register(String key, Class<T> valueType) {
        return this.register(new Option<>(key, valueType));
    }

    public <T> Option<T> register(String key, Class<T> valueType, Predicate<? super T> validator) {
        return this.register(new Option<>(key, valueType, validator));
    }

    public Option<Double> register(String key, DoublePredicate validator) {
        return this.register(key, OptionDataType.DOUBLE, validator::test);
    }

    public Option<Integer> register(String key, IntPredicate validator) {
        return this.register(key, OptionDataType.INTEGER, validator::test);
    }

    public Option<Long> register(String key, LongPredicate validator) {
        return this.register(key, OptionDataType.LONG, validator::test);
    }

    public <T> Option<List<T>> registerList(String key) {
        return this.register(key, OptionDataType.LIST);
    }

    public <T> Option<List<T>> registerList(String key, Predicate<? super List<T>> validator) {
        return this.register(key, OptionDataType.LIST, validator);
    }

    public <T> Option<Set<T>> registerSet(String key) {
        return this.register(key, OptionDataType.SET);
    }

    public <T> Option<Set<T>> registerSet(String key, Predicate<? super Set<T>> validator) {
        return this.register(key, OptionDataType.SET, validator);
    }

    public <K, V> Option<Map<K, V>> registerMap(String key) {
        return this.register(key, OptionDataType.MAP);
    }

    public <K, V> Option<Map<K, V>> registerMap(String key, Predicate<? super Map<K, V>> validator) {
        return this.register(key, OptionDataType.MAP, validator);
    }

    public void setValue(String key, Object value) {
        Option<?> option = this.options.get(key);

        if (option == null) {
            throw new IllegalArgumentException("Unknown option: " + key);
        }

        option.setValue(value);
    }

    @Override
    public String toJson() {
        JsonObject json = new JsonObject();

        JsonObject values = new JsonObject();
        this.options.values().stream()
                .filter(Option::hasValue)
                .forEach(option -> values.add(option.getKey(), Hardlands.GSON.toJsonTree(option.getValue())));

        json.addProperty(IDENTIFIER_PROPERTY, this.identifier);
        json.add(OPTIONS_PROPERTY, values);

        return Hardlands.GSON.toJson(json);
    }

    @Override
    public void fromJson(String json) {
        JsonObject root = Hardlands.GSON.fromJson(json, JsonObject.class);

        String identifier = root.get(IDENTIFIER_PROPERTY).getAsString();
        JsonObject options = root.getAsJsonObject(OPTIONS_PROPERTY);

        if (!this.identifier.equals(identifier)) {
            throw new IllegalArgumentException("Expected option container '%s', but found '%s'".formatted(this.identifier, identifier));
        }

        options.entrySet().forEach(entry -> {
            Option<?> option = this.options.get(entry.getKey());
            if (option == null) return;
            option.setValue(Hardlands.GSON.fromJson(entry.getValue(), option.getValueType()));
        });
    }

    private <T> Option<T> register(Option<T> option) {
        if (this.options.putIfAbsent(option.getKey(), option) != null) {
            throw new IllegalArgumentException("Option already registered: " + option.getKey());
        }

        return option;
    }
}