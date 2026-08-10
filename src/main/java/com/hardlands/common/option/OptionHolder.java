package com.hardlands.common.option;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

public abstract class OptionHolder {

    private final Map<String, Option<?>> registeredOptions = new LinkedHashMap<>();

    protected final <T> Option<T> createOption(String key, Class<T> type) {
        return this.registerOption(new Option<>(key, type));
    }

    protected final <T> Option<T> createOption(String key, Class<T> type, Predicate<? super T> validator) {
        return this.registerOption(new Option<>(key, type, validator));
    }

    protected final Option<Integer> createOption(String key, Class<Integer> type, IntPredicate validator) {
        return this.registerOption(new Option<>(key, type, validator::test));
    }

    public final Option<?> getOption(String key) {
        return this.registeredOptions.get(key);
    }

    public final Map<String, Option<?>> getRegisteredOptions() {
        return Collections.unmodifiableMap(this.registeredOptions);
    }

    public final boolean hasOption(String key) {
        return this.registeredOptions.containsKey(key);
    }

    public final boolean areOptionsValid() {
        return this.registeredOptions.values().stream().allMatch(Option::isValid);
    }

    public final void setOptionValue(String key, Object value) {
        Option<?> option = this.registeredOptions.get(key);

        if (option == null) throw new IllegalArgumentException("Unknown option: " + key);

        option.setValue(value);
    }

    private <T> Option<T> registerOption(Option<T> option) {
        if (this.registeredOptions.putIfAbsent(option.getKey(), option) != null) {
            throw new IllegalArgumentException("Option already registered: " + option.getKey());
        }

        return option;
    }
}