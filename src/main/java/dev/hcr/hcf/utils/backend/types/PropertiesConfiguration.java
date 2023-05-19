package dev.hcr.hcf.utils.backend.types;


import dev.hcr.hcf.utils.backend.ConfigurationType;
import nu.studer.java.util.OrderedProperties;

import java.io.*;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class PropertiesConfiguration extends ConfigurationType {
    private OrderedProperties properties;

    private static boolean update = false;

    private static final Collection<PropertiesConfiguration> configurations = new HashSet<>();

    public PropertiesConfiguration(String fileName) {
        super(fileName);
        OrderedProperties.OrderedPropertiesBuilder builder = new OrderedProperties.OrderedPropertiesBuilder();
        builder.withOrdering(String.CASE_INSENSITIVE_ORDER);
        builder.withSuppressDateInComment(true);
        this.properties = builder.build();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(getFile());
            try {
                properties.load(inputStream);
                if (fileName.toLowerCase().contains("hcf")) {
                    if (properties.containsProperty("version")) {
                        if (getDouble("version") < 8.0) {
                            update = true;
                        }
                    } else {
                        update = true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
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
        configurations.add(this);
    }


    public PropertiesConfiguration(String fileName, String directory) {
        super(fileName, directory);
        OrderedProperties.OrderedPropertiesBuilder builder = new OrderedProperties.OrderedPropertiesBuilder();
        builder.withOrdering(String.CASE_INSENSITIVE_ORDER);
        builder.withSuppressDateInComment(true);
        this.properties = builder.build();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(getFile());
            try {
                properties.load(inputStream);
                if (fileName.toLowerCase().contains("hcf")) {
                    if (properties.containsProperty("version")) {
                        if (getDouble("version") < 9.0) {
                            update = true;
                        }
                    } else {
                        update = true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
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
        configurations.add(this);

    }

    @Override
    public String getString(String key) {
        return properties.getProperty(key);
    }

    @Override
    public Boolean getBoolean(String key) {
        return Boolean.parseBoolean(properties.getProperty(key));
    }

    @Override
    public Integer getInteger(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }

    @Override
    public Double getDouble(String key) {
        return Double.parseDouble(properties.getProperty(key));
    }

    @Override
    public Float getFloat(String key) {
        return Float.parseFloat(properties.getProperty(key));
    }

    @Override
    public Long getLong(String key) {
        try {
            return Long.parseLong(properties.getProperty(key));
        } catch (NumberFormatException ignored) {
            return Long.valueOf(properties.getProperty(key));
        }
    }

    @Override
    public Object get(String key) {
        return properties.getProperty(key);
    }

    @Override
    public boolean has(String key) {
        return properties.containsProperty(key);
    }

    @Override
    public void write(String key, Object object) {
        if (properties == null) {
            OrderedProperties.OrderedPropertiesBuilder builder = new OrderedProperties.OrderedPropertiesBuilder();
            builder.withOrdering(String.CASE_INSENSITIVE_ORDER);
            builder.withSuppressDateInComment(true);
            properties = builder.build();
        }
        try {
            OutputStream outputStream = new FileOutputStream(getFile());
            properties.setProperty(key, object.toString());
            properties.store(outputStream, null);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void preset(File file) {

    }

    public static boolean canUpdate() {
        return update;
    }

    public static void update() {
        // Method for automatically updating files without overwriting all entries.
        PropertiesConfiguration configuration = getPropertiesConfiguration("hcf.properties");
        if (configuration.getDouble("version") < 9) {
            configuration.write("koth-pearl-allowed", false);
            configuration.write("conquest-pearl-allowed", false);
            configuration.write("citadel-pearl-allowed", false);
            configuration.write("debug", false);
        }
        configuration.write("version", 9.0);
        configuration.write("kitmap", false);
    }

    public static PropertiesConfiguration getPropertiesConfiguration(String fileName) {
        return configurations.stream().filter(propertiesConfiguration -> propertiesConfiguration.getFileName().equalsIgnoreCase(fileName)).findAny().orElse(null);
    }
}
