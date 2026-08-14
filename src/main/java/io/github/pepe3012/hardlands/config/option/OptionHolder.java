package io.github.pepe3012.hardlands.config.option;

import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.DoublePredicate;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

public abstract class OptionHolder {

    private final Map<String, Option<?>> registeredOptions = new LinkedHashMap<>();

    protected final <T> Option<T> createOption(String key, OptionDataType dataType) {
        return this.registerOption(new Option<>(key, dataType));
    }

    protected final <T> Option<T> createOption(String key, OptionDataType dataType, Predicate<? super T> validator) {
        return this.registerOption(new Option<>(key, dataType, validator));
    }

    protected final <T> Option<T> createOption(String key, Class<T> valueType) {
        return this.registerOption(new Option<>(key, valueType));
    }

    protected final <T> Option<T> createOption(String key, Class<T> valueType, Predicate<? super T> validator) {
        return this.registerOption(new Option<>(key, valueType, validator));
    }

    protected final Option<Double> createOption(String key, DoublePredicate validator) {
        return this.registerOption(new Option<>(key, OptionDataType.DOUBLE, validator::test));
    }

    protected final Option<Integer> createOption(String key, IntPredicate validator) {
        return this.registerOption(new Option<>(key, OptionDataType.INTEGER, validator::test));
    }

    protected final Option<Long> createOption(String key, LongPredicate validator) {
        return this.registerOption(new Option<>(key, OptionDataType.LONG, validator::test));
    }

    protected final <T> Option<List<T>> createListOption(String key) {
        return this.registerOption(new Option<>(key, OptionDataType.LIST));
    }

    protected final <T> Option<List<T>> createListOption(String key, Predicate<? super List<T>> validator) {
        return this.registerOption(new Option<>(key, OptionDataType.LIST, validator));
    }

    protected final <K, V> Option<Map<K, V>> createMapOption(String key) {
        return this.registerOption(new Option<>(key, OptionDataType.MAP));
    }

    protected final <K, V> Option<Map<K, V>> createMapOption(String key, Predicate<? super Map<K, V>> validator) {
        return this.registerOption(new Option<>(key, OptionDataType.MAP, validator));
    }

    protected final <T> Option<Set<T>> createSetOption(String key) {
        return this.registerOption(new Option<>(key, OptionDataType.SET));
    }

    protected final <T> Option<Set<T>> createSetOption(String key, Predicate<? super Set<T>> validator) {
        return this.registerOption(new Option<>(key, OptionDataType.SET, validator));
    }

    public final boolean areOptionsValid() {
        return this.registeredOptions.values().stream().allMatch(Option::isValid);
    }

    public final @Nullable Option<?> getOption(String key) {
        return this.registeredOptions.get(key);
    }

    public final Map<String, Option<?>> getRegisteredOptions() {
        return Collections.unmodifiableMap(this.registeredOptions);
    }

    public final boolean hasOption(String key) {
        return this.registeredOptions.containsKey(key);
    }

    public final void setOptionValue(String key, Object value) {
        Option<?> option = this.registeredOptions.get(key);

        if (option == null) {
            throw new IllegalArgumentException("Unknown option: " + key);
        }

        option.setValue(value);
    }

    private <T> Option<T> registerOption(Option<T> option) {
        String key = option.getKey();

        if (this.registeredOptions.putIfAbsent(key, option) != null) {
            throw new IllegalArgumentException("Option already registered: " + key);
        }

        return option;
    }
}