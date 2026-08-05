package com.hardlands.option;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Stores a configurable value associated with a string key.
 *
 * <p>The default validation rule considers only non-null values valid. A custom rule may be
 * supplied when the option is created. Default and current values may still be invalid because
 * validation is performed explicitly rather than enforced during assignment.</p>
 *
 * @param <T> the type of value stored by this option
 */
public final class Option<T> {
    private final String key;
    private final T defaultValue;
    private final Predicate<? super T> validator;

    private T value;

    /**
     * Creates an option whose valid values must not be {@code null}.
     *
     * @param key the identifier associated with the option
     * @param defaultValue the default value, which may be {@code null}
     */
    public Option(String key, T defaultValue) {
        this(key, defaultValue, Objects::nonNull);
    }

    /**
     * Creates an option with a custom validation rule.
     *
     * @param key the identifier associated with the option
     * @param defaultValue the default value
     * @param validator the rule used to validate values
     */
    public Option(String key, T defaultValue, Predicate<? super T> validator) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.validator = Objects.requireNonNull(validator, "validator");
        this.value = defaultValue;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getKey() {
        return this.key;
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public T getValue() {
        return this.value;
    }

    public boolean isValid() {
        return this.validator.test(this.value);
    }
}