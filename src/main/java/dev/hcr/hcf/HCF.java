package dev.hcr.hcf;

import dev.hcr.hcf.commands.admin.EconomyCommand;
import dev.hcr.hcf.commands.donor.ClaimBonusChestCommand;
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
import dev.hcr.hcf.listeners.player.MiningListener;
import dev.hcr.hcf.listeners.player.PlayerPacketListener;
import dev.hcr.hcf.packets.PacketHandler;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.backend.ConfigFile;
import dev.hcr.hcf.utils.backend.types.PropertiesConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class HCF extends JavaPlugin {
    private static HCF plugin;
    private final NumberFormat format = NumberFormat.getCurrencyInstance();
    private MongoImplementation database;
    private FactionRegenTask regenTask;
    private PacketHandler packetHandler;
    private final List<ConfigFile> files = new ArrayList<>();

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
        User.getUsers().forEach(user -> getMongoImplementation().appendUserDataSync(user.save()));

        // TODO: 2/6/2022 Create mongodb database backup upon disable
        List<String> command = Arrays.asList(
                "mongodump",
                "--out", "/backup/" // DEFAULTS TO working directory
        );

    }

    private List<String> readOutputHelper(InputStream inputStream) throws IOException {
        try (BufferedReader output = new BufferedReader(new InputStreamReader(inputStream))) {
            return output.lines().collect(Collectors.toList());
        }
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
        getCommand("claimbonuschest").setExecutor(new ClaimBonusChestCommand());
        getCommand("economy").setExecutor(new EconomyCommand());
        getCommand("faction").setExecutor(new DefaultFactionCommand());
    }

    private void implementDatabases() {
        database = new MongoImplementation(this);
    }

    private void loadFactions() {
        regenTask = new FactionRegenTask();
        regenTask.runTaskTimerAsynchronously(this, 20L, 20L);
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
    }

    private void saveFactions() {
        for (Faction faction : Faction.getFactions()) {
            database.appendFactionData(faction.save());
        }
    }

    private void registerEvents() {
        // Handle packet managers
        packetHandler = new PacketHandler();
        Arrays.asList(new UserListener(), new FactionListener(), new FactionClaimingListener(), new MiningListener(), new PlayerPacketListener()).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
    }

    public PacketHandler getPacketHandler() {
        return packetHandler;
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
