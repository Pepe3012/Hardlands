package io.github.pepe3012.hardlands.data.option;

import java.util.function.Predicate;

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

    public Option(String key, Class<T> valueType, OptionDataType dataType, Predicate<? super T> validator) {
        if (key.isBlank()) throw new IllegalArgumentException("Option key cannot be blank");

        this.key = key;
        this.valueType = valueType;
        this.dataType = dataType;
        this.validator = validator;
    }

    public String getKey() {
        return this.key;
    }

    public Class<T> getValueType() {
        return this.valueType;
    }

    public OptionDataType getDataType() {
        return this.dataType;
    }

    public Predicate<? super T> getValidator() {
        return this.validator;
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        if (!this.valueType.isInstance(value)) {
            throw new IllegalArgumentException("Option '%s' requires a value of type %s".formatted(this.key, this.valueType.getSimpleName()));
        }

        T typedValue = this.valueType.cast(value);

        if (!this.validator.test(typedValue)) {
            throw new IllegalArgumentException("Invalid value for option '%s'".formatted(this.key));
        }

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

    //wtfd
    private static <T> Class<T> resolveValueType(OptionDataType dataType) {
        if (dataType == OptionDataType.CUSTOM) {
            throw new IllegalArgumentException("Custom options require an explicit Java type");
        }

        return (Class<T>) dataType.getJavaType();
    }
}