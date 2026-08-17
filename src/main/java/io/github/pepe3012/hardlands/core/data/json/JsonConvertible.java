package io.github.pepe3012.hardlands.core.data.json;

import com.google.gson.JsonElement;

public interface JsonConvertible {

    void fromJson(JsonElement json);

    JsonElement toJson();
}