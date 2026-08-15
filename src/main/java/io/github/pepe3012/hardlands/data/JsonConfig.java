package io.github.pepe3012.hardlands.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public final class JsonConfig<T> {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path path;
    private final Type type;

    public JsonConfig(final Plugin plugin, String fileName, Type type) {
        this.path = plugin.getDataPath().resolve(fileName);
        this.type = type;
    }

    public void save(T value) {
        try {
            Files.createDirectories(this.path.getParent());

            try (Writer writer = Files.newBufferedWriter(this.path, StandardCharsets.UTF_8)) {
                GSON.toJson(value, this.type, writer);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to save " + path, exception);
        }
    }

    public Optional<T> load() {
        if (!Files.exists(this.path)) {
            return Optional.empty();
        }

        try (Reader reader = Files.newBufferedReader(this.path, StandardCharsets.UTF_8)) {
            return Optional.ofNullable(GSON.fromJson(reader, this.type));
        } catch (IOException | JsonParseException exception) {
            throw new IllegalStateException("Failed to load " + this.path, exception);
        }
    }
}