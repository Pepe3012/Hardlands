package com.hardlands.option;

import lombok.Getter;
import org.jspecify.annotations.NonNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

public final class OptionContainer {

    @Getter private final Map<String, Option<?>> options = new LinkedHashMap<>();

    public <T> Option<T> create(String key, T defaultValue) {
        Option<T> option = new Option<>(key, defaultValue);
        this.options.put(key, option);
        return option;
    }

    public <T> Option<T> create(String key, T defaultValue, Predicate<? super T> validator) {
        Option<T> option = new Option<>(key, defaultValue, validator);
        this.options.put(key, option);
        return option;
    }

    public @NonNull Option<Integer> create(String key, int defaultValue, IntPredicate validator) {
        Option<Integer> option = new Option<>(key, defaultValue, validator::test);

        this.options.put(key, option);
        return option;
    }

    public <T> Option<T> get(String key) {
        return (Option<T>) this.options.get(key);
    }

    public boolean validate() {
        return this.options.values().stream().allMatch(Option::isValid);
    }
}