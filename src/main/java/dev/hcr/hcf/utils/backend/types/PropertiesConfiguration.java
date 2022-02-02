package dev.hcr.hcf.utils.backend.types;

import dev.hcr.hcf.utils.backend.ConfigurationType;

import java.io.*;
import java.util.Properties;

public class PropertiesConfiguration extends ConfigurationType {
    private final File file;
    private final Properties properties;

    public PropertiesConfiguration(File file) {
        super(file);
        this.file = file;
        this.properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!properties.containsKey("max-team-size")) {
            // If the file does not exist (aka entry keys are null
            // Create the file with preset values
            setup();
        }
    }

    public Properties getProperties() {
        return properties;
    }

    private void setup() {
        set("max-team-size", 6);
        set("max-allies", 0);
        set("max-dtr", 5.5);
        set("dtr-multiplier", 1.1);
        set("dtr-increment-per-second", 0.0005);
        set("dtr-loss-per-death", 1.0);
        set("team-damage", false);
    }

    @Override
    public String getString(String path) {
        return properties.getProperty(path);
    }

    @Override
    public Boolean getBoolean(String path) {
        return Boolean.parseBoolean(properties.getProperty(path));
    }

    @Override
    public Integer getInteger(String path) {
        return Integer.parseInt(properties.getProperty(path));
    }

    @Override
    public Double getDouble(String path) {
        return Double.parseDouble(properties.getProperty(path));
    }

    @Override
    public Float getFloat(String path) {
        return Float.parseFloat(properties.getProperty(path));
    }

    @Override
    public Long getLong(String path) {
        return Long.parseLong(properties.getProperty(path));
    }

    @Override
    public Object get(String path) {
        return properties.get(path);
    }

    @Override
    public void set(String path, Object value) {
        try {
            OutputStream outputStream = new FileOutputStream(file);
            properties.setProperty(path, value.toString());
            properties.store(outputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
