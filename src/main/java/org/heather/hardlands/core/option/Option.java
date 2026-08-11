package org.heather.hardlands.core.option;

import lombok.Getter;

import java.util.Objects;
import java.util.function.Predicate;

@Getter
public final class Option<T> {

    private final String key;
    private final Class<T> valueType;
    private final OptionDataType dataType;
    private final Predicate<? super T> validator;

    private T value;

    public Option(String key, OptionDataType dataType) {
        this(key, dataType, _ -> true);
    }

    public Option(String key, OptionDataType dataType, Predicate<? super T> validator) {
        this(key, resolveValueType(dataType), dataType, validator);
    }

    public Option(String key, Class<T> valueType) {
        this(key, valueType, _ -> true);
    }

    public Option(String key, Class<T> valueType, Predicate<? super T> validator) {
        this(key, valueType, OptionDataType.fromJavaType(valueType), validator);
    }

    private Option(String key, Class<T> valueType, OptionDataType dataType, Predicate<? super T> validator) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Option key cannot be blank");
        }

        this.key = key;
        this.valueType = Objects.requireNonNull(valueType, "Value type cannot be null");
        this.dataType = Objects.requireNonNull(dataType, "Data type cannot be null");
        this.validator = Objects.requireNonNull(validator, "Validator cannot be null");
    }

    public void setValue(Object value) {
        if (!this.valueType.isInstance(value)) {
            throw new IllegalArgumentException("Option '" + this.key + "' requires a value of type " + this.valueType.getSimpleName());
        }

        T typedValue = this.valueType.cast(value);

        if (!this.validator.test(typedValue)) {
            throw new IllegalArgumentException("Invalid value for option '" + this.key + "'");
        }

        this.value = typedValue;
    }

    public boolean hasValue() {
        return this.value != null;
    }

    public boolean isValid() {
        return this.hasValue() && this.validator.test(this.value);
    }

    public void clearValue() {
        this.value = null;
    }

    private static <T> Class<T> resolveValueType(OptionDataType dataType) {
        Objects.requireNonNull(dataType, "Data type cannot be null");

        if (dataType == OptionDataType.CUSTOM) {
            throw new IllegalArgumentException("Custom options require an explicit Java type");
        }

        return (Class<T>) dataType.getJavaType();
    }
}