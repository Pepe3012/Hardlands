package com.hardlands.scenario;

public class Option<T> {
    private final String key;
    private T value;

    private Option(String key, T value) {
        this.key = key;
        this.value = value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getKey() {
        return this.key;
    }

    public T getValue() {
        return this.value;
    }

    public static <T> Option<T> create(String key, T value) {
        return new Option<>(key, value);
    }
}