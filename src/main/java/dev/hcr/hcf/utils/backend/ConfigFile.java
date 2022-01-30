package dev.hcr.hcf.utils.backend;

import com.google.common.base.Charsets;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigFile {
    public static ConfigFile instance;
    private final JavaPlugin plugin;
    private YamlConfiguration configuration;
    private String name;
    private File file;
    private FileConfiguration newConfig = null;

    public ConfigFile(String name, JavaPlugin plugin) {
        this.name = name;
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), name + ".yml");
        if (!this.file.exists()) {
            plugin.saveResource(name + ".yml", false);
        }
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
        instance = this;
    }

    public ConfigFile(String name, String subDirectory, JavaPlugin plugin) {
        this.name = name;
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder() + "/" + subDirectory + "/", name + ".yml");
        if (!this.file.exists()) {
            plugin.saveResource(subDirectory + "/" + name + ".yml", false);
        }
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
        instance = this;
    }

    public String getName() {
        return name;
    }

    public YamlConfiguration getConfiguration() {
        return configuration;
    }

    public boolean getBoolean(String path) {
        return (this.configuration.contains(path)) && (this.configuration.getBoolean(path));
    }

    public double getDouble(String path) {
        if (this.configuration.contains(path)) {
            return this.configuration.getDouble(path);
        }
        return 0.0D;
    }

    public int getInt(String path) {
        if (this.configuration.contains(path)) {
            return this.configuration.getInt(path);
        }
        return 0;
    }

    public String getString(String path) {
        if (this.configuration.contains(path)) {
            return ChatColor.translateAlternateColorCodes('&', this.configuration.getString(path));
        }
        return "String at path: " + path + " not found!";
    }

    public String getUnColoredString(String path) {
        if (this.configuration.contains(path)) {
            return this.configuration.getString(path);
        }
        return "String at path: " + path + " not found!";
    }

    public List<String> getStringList(String path) {
        if (this.configuration.contains(path)) {
            ArrayList<String> strings = new ArrayList<String>();
            for (String string : this.configuration.getStringList(path)) {
                strings.add(ChatColor.translateAlternateColorCodes('&', string));
            }
            return strings;
        }
        return Arrays.asList("String List at path: " + path + " not found!");
    }

    public void set(String path, String key) {
        this.configuration.set(path, key);
        save();
    }

    public void load() {
        this.file = new File(plugin.getDataFolder(), name + ".yml");
        if (!this.file.exists()) {
            plugin.saveResource(name + ".yml", false);
        }
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
    }

    public InputStream getResource(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }

        try {
            URL url = this.getClass().getClassLoader().getResource(filename);

            if (url == null) {
                return null;
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }

    public void reload() {
        newConfig = YamlConfiguration.loadConfiguration(file);
        final InputStream defConfigStream = getResource("config.yml");
        if (defConfigStream == null) {
            return;
        }
        final YamlConfiguration defConfig;
        defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8));
        newConfig.setDefaults(defConfig);
    }
    public void save() {
        try {
            this.configuration.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
