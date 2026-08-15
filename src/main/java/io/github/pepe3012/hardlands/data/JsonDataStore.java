package io.github.pepe3012.hardlands.data;

import io.github.pepe3012.hardlands.Hardlands;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public final class JsonDataStore<T> {

    private final Path path;
    private final Class<T> type;

    public JsonDataStore(Path path, Class<T> type) {
        this.path = path;
        this.type = type;
    }

    public void write(T value) {
        try {
            Files.createDirectories(this.path.getParent());
            Files.writeString(this.path, Hardlands.GSON.toJson(value), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to write " + this.path.getFileName(), exception);
        }
    }

    public Optional<T> read() {
        if (!Files.exists(this.path)) {
            return Optional.empty();
        }

        try {
            return Optional.ofNullable(Hardlands.GSON.fromJson(Files.readString(this.path, StandardCharsets.UTF_8), this.type));
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read " + this.path.getFileName(), exception);
        }
    }
}