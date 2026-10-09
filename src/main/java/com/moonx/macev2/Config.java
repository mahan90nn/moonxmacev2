package com.moonx.macev2;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Config {

    private final File file;
    private Map<String, Object> data;

    public Config(@NotNull File file) {
        this.file = file;
        this.data = new LinkedHashMap<>();
        load();
    }

    public void load() {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            data = new LinkedHashMap<>();
            return;
        }
        try (InputStream in = new FileInputStream(file)) {
            Yaml yaml = new Yaml();
            Object loaded = yaml.load(in);
            if (loaded instanceof Map) {
                data = (Map<String, Object>) loaded;
            } else {
                data = new LinkedHashMap<>();
            }
        } catch (IOException e) {
            data = new LinkedHashMap<>();
        }
    }

    public void save() {
        try {
            file.getParentFile().mkdirs();
            Yaml yaml = new Yaml();
            try (FileWriter writer = new FileWriter(file)) {
                yaml.dump(data, writer);
            }
        } catch (IOException e) {
            // ignore
        }
    }

    public void reload() {
        load();
    }

    public boolean getBoolean(@NotNull String path, boolean def) {
        Object val = get(path);
        if (val instanceof Boolean) return (Boolean) val;
        return def;
    }

    public int getInt(@NotNull String path, int def) {
        Object val = get(path);
        if (val instanceof Number) return ((Number) val).intValue();
        return def;
    }

    public double getDouble(@NotNull String path, double def) {
        Object val = get(path);
        if (val instanceof Number) return ((Number) val).doubleValue();
        return def;
    }

    public float getFloat(@NotNull String path, float def) {
        Object val = get(path);
        if (val instanceof Number) return ((Number) val).floatValue();
        return def;
    }

    @Nullable
    public String getString(@NotNull String path, @Nullable String def) {
        Object val = get(path);
        return val != null ? val.toString() : def;
    }

    @Nullable
    public Object get(@NotNull String path) {
        String[] parts = path.split("\\.");
        Object current = data;
        for (String part : parts) {
            if (current instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) current;
                if (!map.containsKey(part)) return null;
                current = map.get(part);
            } else {
                return null;
            }
        }
        return current;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public Map<String, Object> getMap(@NotNull String path) {
        Object val = get(path);
        if (val instanceof Map) {
            return (Map<String, Object>) val;
        }
        return null;
    }

    public void set(@NotNull String path, @Nullable Object value) {
        String[] parts = path.split("\\.");
        Map<String, Object> current = data;
        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            if (!current.containsKey(part) || !(current.get(part) instanceof Map)) {
                current.put(part, new LinkedHashMap<String, Object>());
            }
            current = (Map<String, Object>) current.get(part);
        }
        current.put(parts[parts.length - 1], value);
    }

    public @NotNull File getFile() {
        return file;
    }
}