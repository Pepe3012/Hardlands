package com.hardlands.scenario.option;

public class StringOption extends Option<String> {
    public StringOption(String key, String defaultValue) {
        super(key, Type.STRING, defaultValue);
    }
}