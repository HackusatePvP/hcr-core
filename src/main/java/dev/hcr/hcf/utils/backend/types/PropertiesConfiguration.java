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
        return Long.parseLong(properties.getProperty(key));
    }

    @Override
    public Object get(String key) {
        return properties.getProperty(key);
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
        if (file.getName().toLowerCase().contains("hcf")) {
            write("version", 8.0);
            write("debug", false);
            write("default-balance", 500);
            write("koth-pearl-allowed", false);
            write("conquest-pearl-allowed", false);
            write("citadel-pearl-allowed", false);
        }
        if (file.getName().toLowerCase().contains("faction")) {
            write("max-team-size", 6);
            write("max-allies", 0);
            write("max-dtr", 5.5);
            write("dtr-multiplier", 1.1);
            write("dtr-increment-per-second", 0.0005);
            write("dtr-loss-per-death", 1.0);
            write("ally-damage", false);
            write("team-damage", false);
            write("warzone-radius", 500);
            write("warzone-build-radius", 300);
            write("regen-start-delay", 30L);
            write("regen-delay-time", 3L);
            write("regen-increment", 0.03);
            write("max-bard-energy", 120);
        }
        if (file.getName().toLowerCase().contains("claim")) {
            write("max-claims-in-chunks", 16);
            write("claim-price-per-block", 9.75);
            write("unclaim-price-per-block", 9.75);
        }
        if (file.getName().toLowerCase().contains("pvpclass")) {
            System.out.println("Overwritting");
            write("archer-speed-duration", 20*15);
            write("archer-speed-amplifier", 3);
            write("archer-resistance-duration", 20*15);
            write("archer-resistance-amplifier", 4);
            write("bard-speed-duration", 20*20);
            write("bard-speed-amplifier", 2);
            write("bard-resistance-duration", 20*20);
            write("bard-resistance-amplifier", 2);
            write("bard-strength-duration", 20*20);
            write("bard-strength-amplifier", 1);
            write("bard-regeneration-duration", 20*20);
            write("bard-regeneration-amplifier", 4);
            write("bard-jump-boost-duration", 20*20);
            write("bard-jump-boost-amplifier", 4);
            write("bard-wither-duration", 20*10);
            write("bard-wither-amplifier", 1);
            write("rogue-speed-duration", 20*10);
            write("rogue-speed-amplifier", 4);
            write("rogue-jump-boost-duration", 20*10);
            write("rogue-jump-boost-amplifier", 6);
        }
        if (file.getName().toLowerCase().contains("database")) {
            write("main-loader", "Mongo");
            write("redis-communication", false);
            write("redis-channel", "hcf-com");
        }
        if (file.getName().toLowerCase().contains("mongo")) {
            write("host", "127.0.0.1");
            write("port", 27017);
            write("database", "HCF");
            write("db-auth", false);
            write("db-auth-db", "admin");
            write("db-auth-user", "admin");
            write("db-auth-password", "password");
        }
    }

    public static boolean canUpdate() {
        return update;
    }

    public static void update() {
        // Method for automatically updating files without overwriting all entries.
        PropertiesConfiguration configuration = getPropertiesConfiguration("hcf.properties");
        configuration.write("version", 8.0);
        configuration.write("koth-pearl-allowed", false);
        configuration.write("conquest-pearl-allowed", false);
        configuration.write("citadel-pearl-allowed", false);
        configuration.write("debug", false);
    }

    public static PropertiesConfiguration getPropertiesConfiguration(String fileName) {
        return configurations.stream().filter(propertiesConfiguration -> propertiesConfiguration.getFileName().equalsIgnoreCase(fileName)).findAny().orElse(null);
    }
}
