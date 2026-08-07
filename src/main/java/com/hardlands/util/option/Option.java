package com.hardlands.util.option;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Represents a configurable value with an optional validation rule.
 *
 * @param <T> the value type
 */
public final class Option<T> {

    @Getter private final String key;
    @Getter private final T defaultValue;
    private final Predicate<? super T> validator;

    @Getter @Setter private T value;

    /**
     * Creates an option that accepts non-null values.
     */
    public Option(String key, T defaultValue) {
        this(key, defaultValue, Objects::nonNull);
    }

    /**
     * Creates an option with the specified validation rule.
     */
    public Option(String key, T defaultValue, Predicate<? super T> validator) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.validator = validator;
        this.value = defaultValue;
    }

    /**
     * Returns whether the current value is valid.
     */
    public boolean isValid() {
        return this.validator.test(this.value);
    }
}