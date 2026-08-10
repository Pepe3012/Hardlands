package com.hardlands.common.option;

import lombok.Getter;

import java.util.function.Predicate;

public final class Option<T> {

    @Getter private final String key;
    @Getter private final Class<T> type;
    private final Predicate<? super T> validator;

    @Getter private T value;

    public Option(String key, Class<T> type) {
        this(key, type, _ -> true);
    }

    public Option(String key, Class<T> type, Predicate<? super T> validator) {
        this.key = key;
        this.type = type;
        this.validator = validator;
    }

    public void setValue(Object value) {
        if (!this.type.isInstance(value)) throw new IllegalArgumentException("Option '" + this.key + "' requires a value of type " + this.type.getSimpleName());

        T typedValue = this.type.cast(value);

        if (!this.validator.test(typedValue)) throw new IllegalArgumentException("Invalid value for option '" + this.key + "'");

        this.value = typedValue;
    }

    public boolean hasValue() {
        return this.value != null;
    }

    public boolean isValid() {
        return this.value != null && this.validator.test(this.value);
    }

    public void clearValue() {
        this.value = null;
    }
}