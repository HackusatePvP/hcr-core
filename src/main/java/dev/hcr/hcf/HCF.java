package dev.hcr.hcf;

import dev.hcr.hcf.commands.admin.EconomyCommand;
import dev.hcr.hcf.commands.players.BalanceCommand;
import dev.hcr.hcf.factions.commands.member.DefaultFactionCommand;
import dev.hcr.hcf.factions.commands.FactionCommandManager;
import dev.hcr.hcf.factions.structure.regen.FactionRegenTask;
import dev.hcr.hcf.factions.types.SafeZoneFaction;
import dev.hcr.hcf.listeners.FactionListener;
import dev.hcr.hcf.listeners.UserListener;
import dev.hcr.hcf.utils.backend.ConfigFile;
import dev.hcr.hcf.utils.backend.types.PropertiesConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class HCF extends JavaPlugin {
    private static HCF plugin;
    private final NumberFormat format = NumberFormat.getCurrencyInstance();
    private FactionRegenTask regenTask;
    private List<ConfigFile> files = new ArrayList<>();

    @Override
    public void onEnable() {
        plugin = this;
        loadConfigurationFiles();
        registerCommands();
        loadFactions();
        registerEvents();
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
        getCommand("balance").setExecutor(new BalanceCommand());
        getCommand("economy").setExecutor(new EconomyCommand());
        getCommand("faction").setExecutor(new DefaultFactionCommand());
    }

    private void loadFactions() {
        regenTask = new FactionRegenTask();
        regenTask.runTaskTimerAsynchronously(this, 20L, 20L);
        new SafeZoneFaction("safezone");
    }

    private void registerEvents() {
        Arrays.asList(new UserListener(), new FactionListener()).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
    }

    public FactionRegenTask getRegenTask() {
        return regenTask;
    }

    public NumberFormat getFormat() {
        return format;
    }

    public static HCF getPlugin() {
        return plugin;
    }
}
