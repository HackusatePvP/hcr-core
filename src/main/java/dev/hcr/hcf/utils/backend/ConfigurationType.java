package dev.hcr.hcf.utils.backend;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class ConfigurationType {
    private static final Map<String, ConfigurationType> configurationFiles = new HashMap<>();

    public ConfigurationType(File file) {
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        configurationFiles.put(file.getName(), this);
    }

    public static ConfigurationType getConfiguration(String name) {
        return configurationFiles.get(name);
    }

    public abstract String getString(String path);

    public abstract Boolean getBoolean(String path);

    public abstract Integer getInteger(String path);

    public abstract Double getDouble(String path);

    public abstract Float getFloat(String path);

    public abstract Long getLong(String path);

    public abstract Object get(String path);

    public abstract void set(String path, Object value);

}
