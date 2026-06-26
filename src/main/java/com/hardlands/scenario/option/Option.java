package com.hardlands.scenario.option;

public abstract class Option<T> {
    private final String key;
    private final Type type;
    private T value;

    protected Option(String key, Type type, T value) {
        this.key = key;
        this.type = type;
        this.value = value;
    }

    public void setValue(T value) { this.value = value; }

    public String getKey() { return key; }
    public Type getType() { return type; }
    public T getValue() { return value; }

    public enum Type { BOOLEAN, FLOAT, STRING }
}