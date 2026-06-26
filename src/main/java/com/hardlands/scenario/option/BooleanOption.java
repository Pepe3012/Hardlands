package com.hardlands.scenario.option;

public class BooleanOption extends Option<Boolean> {
    public BooleanOption(String key, boolean defaultValue) {
        super(key, Type.BOOLEAN, defaultValue);
    }
}