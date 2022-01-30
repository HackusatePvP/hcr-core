package dev.hcr.hcf;

import dev.hcr.hcf.factions.commands.member.DefaultFactionCommand;
import dev.hcr.hcf.factions.commands.FactionCommandManager;
import dev.hcr.hcf.factions.types.SafeZoneFaction;
import dev.hcr.hcf.listeners.UserListener;
import dev.hcr.hcf.utils.backend.ConfigFile;
import dev.hcr.hcf.utils.backend.types.PropertiesConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class HCF extends JavaPlugin {
    private static HCF plugin;
    private List<ConfigFile> files = new ArrayList<>();

    @Override
    public void onEnable() {
        plugin = this;
        loadConfigurationFiles();
        registerCommands();
        loadFactions();
        Bukkit.getPluginManager().registerEvents(new UserListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void loadConfigurationFiles() {
        files.addAll(Arrays.asList(
                new ConfigFile("config", this)
        ));
        File directory = new File(plugin.getDataFolder(), "factions");
        directory.mkdirs();
        new PropertiesConfiguration(new File(directory, "faction.properties"));
    }

    private void registerCommands() {
        new FactionCommandManager();
        getCommand("faction").setExecutor(new DefaultFactionCommand());
    }

    private void loadFactions() {
        new SafeZoneFaction("safezone");
    }

    public static HCF getPlugin() {
        return plugin;
    }
}
