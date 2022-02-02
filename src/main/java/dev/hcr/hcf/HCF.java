package dev.hcr.hcf;

import dev.hcr.hcf.commands.admin.EconomyCommand;
import dev.hcr.hcf.commands.players.BalanceCommand;
import dev.hcr.hcf.databases.MongoImplementation;
import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.commands.member.DefaultFactionCommand;
import dev.hcr.hcf.factions.commands.FactionCommandManager;
import dev.hcr.hcf.factions.structure.regen.FactionRegenTask;
import dev.hcr.hcf.factions.types.SafeZoneFaction;
import dev.hcr.hcf.factions.types.WarzoneFaction;
import dev.hcr.hcf.factions.types.WildernessFaction;
import dev.hcr.hcf.listeners.factions.FactionClaimingListener;
import dev.hcr.hcf.listeners.factions.FactionListener;
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
    private MongoImplementation database;
    private FactionRegenTask regenTask;
    private List<ConfigFile> files = new ArrayList<>();

    @Override
    public void onEnable() {
        plugin = this;
        loadConfigurationFiles();
        registerCommands();
        implementDatabases();
        loadFactions();
        registerEvents();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        saveFactions();
    }

    private void loadConfigurationFiles() {
        files.addAll(Arrays.asList(
                new ConfigFile("config", this),
                new ConfigFile("database", this)
        ));
        File directory = new File(plugin.getDataFolder(), "factions");
        directory.mkdirs();
        new PropertiesConfiguration(new File(directory, "faction.properties"));
    }

    public ConfigFile getConfiguration(String file) {
        return files.stream().filter(configFile -> configFile.getName().equalsIgnoreCase(file)).findAny().orElse(null);
    }

    private void registerCommands() {
        new FactionCommandManager();
        getCommand("balance").setExecutor(new BalanceCommand());
        getCommand("economy").setExecutor(new EconomyCommand());
        getCommand("faction").setExecutor(new DefaultFactionCommand());
    }

    private void implementDatabases() {
        database = new MongoImplementation(this);
    }

    private void loadFactions() {
        database.loadFactions();
        if (Faction.getWilderness() == null) {
            new WildernessFaction();
        }
        if (Faction.getWarzone() == null) {
            new WarzoneFaction();
        }
        if (Faction.getSafeZone() == null) {
            new SafeZoneFaction();
        }
        regenTask = new FactionRegenTask();
        regenTask.runTaskTimerAsynchronously(this, 20L, 20L);

    }

    void saveFactions() {
        for (Faction faction : Faction.getFactions()) {
            database.appendFactionData(faction.save());
        }
    }

    private void registerEvents() {
        Arrays.asList(new UserListener(), new FactionListener(), new FactionClaimingListener()).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
    }

    public MongoImplementation getMongoImplementation() {
        return database;
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
