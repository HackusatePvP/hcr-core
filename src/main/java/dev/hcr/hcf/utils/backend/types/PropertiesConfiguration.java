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
    }

    public Properties getProperties() {
        return properties;
    }

    @Override
    public String getString(String path) {
        return properties.getProperty(path);
    }

    @Override
    public Boolean getBoolean(String path) {
        return (Boolean) properties.get(path);
    }

    @Override
    public Integer getInteger(String path) {
        return (Integer) properties.get(path);
    }

    @Override
    public Double getDouble(String path) {
        return (Double) properties.get(path);
    }

    @Override
    public Float getFloat(String path) {
        return (Float) properties.get(path);
    }

    @Override
    public Long getLong(String path) {
        return (Long) properties.get(path);
    }

    @Override
    public Object get(String path) {
        return properties.get(path);
    }

    @Override
    public void set(String path, Object value) {
        try {
            OutputStream outputStream = new FileOutputStream(file);
            if (value instanceof String) {
                properties.setProperty(path, (String) value);
            } else if (value instanceof Boolean) {
                properties.setProperty(path, ((Boolean) value).toString());
            } else if (value instanceof Integer) {
                properties.setProperty(path, ((Integer) value).toString());
            } else if (value instanceof Double) {
                properties.setProperty(path, ((Double) value).toString());
            } else if (value instanceof Float) {
                properties.setProperty(path, ((Float) value).toString());
            } else if (value instanceof Long) {
                properties.setProperty(path, ((Long) value).toString());
            } else {
                properties.setProperty(path, value.toString());
            }
            properties.store(outputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
