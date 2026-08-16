package io.github.pepe3012.hardlands.data.json;

public interface JsonConvertible {

    String toJson();

    void fromJson(String json);
}