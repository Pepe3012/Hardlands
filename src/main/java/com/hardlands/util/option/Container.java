package com.hardlands.util.option;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

public final class Container {

    @Getter private final Map<String, Option<?>> options = new LinkedHashMap<>();

    public <T> Option<T> create(String key, T defaultValue) {
        return this.register(new Option<>(key, defaultValue));
    }

    public <T> Option<T> create(String key, T defaultValue, Predicate<? super T> validator) {
        return this.register(new Option<>(key, defaultValue, validator));
    }

    public Option<Integer> create(String key, int defaultValue, IntPredicate validator) {
        return this.register(new Option<>(key, defaultValue, validator::test));
    }

    public <T> Option<T> get(String key) {
        return (Option<T>) this.options.get(key);
    }

    public boolean isValid() {
        return this.options.values().stream().allMatch(Option::isValid);
    }

    private <T> Option<T> register(Option<T> option) {
        this.options.put(option.getKey(), option);
        return option;
    }
}