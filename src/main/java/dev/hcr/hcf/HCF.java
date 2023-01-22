package dev.hcr.hcf;

import dev.hcr.hcf.commands.admin.EconomyCommand;
import dev.hcr.hcf.commands.admin.SOTWCommand;
import dev.hcr.hcf.commands.donor.ClaimBonusChestCommand;
import dev.hcr.hcf.commands.players.BalanceCommand;
import dev.hcr.hcf.commands.players.PayCommand;
import dev.hcr.hcf.commands.players.PvPTimerCommand;
import dev.hcr.hcf.commands.players.lives.DefaultLivesCommand;
import dev.hcr.hcf.commands.players.lives.LivesCommandManager;
import dev.hcr.hcf.commands.staff.GlowstoneScannerCommand;
import dev.hcr.hcf.databases.IStorage;
import dev.hcr.hcf.databases.impl.MongoStorage;
import dev.hcr.hcf.deathbans.DeathBanListener;
import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.commands.member.DefaultFactionCommand;
import dev.hcr.hcf.factions.commands.FactionCommandManager;
import dev.hcr.hcf.factions.structure.regen.FactionRegenTask;
import dev.hcr.hcf.factions.types.GlowStoneMountainFaction;
import dev.hcr.hcf.factions.types.SafeZoneFaction;
import dev.hcr.hcf.factions.types.WarzoneFaction;
import dev.hcr.hcf.factions.types.WildernessFaction;
import dev.hcr.hcf.factions.types.roads.EastRoad;
import dev.hcr.hcf.factions.types.roads.NorthRoad;
import dev.hcr.hcf.factions.types.roads.SouthRoad;
import dev.hcr.hcf.factions.types.roads.WestRoad;
import dev.hcr.hcf.hooks.AquaCoreHook;
import dev.hcr.hcf.hooks.PluginHook;
import dev.hcr.hcf.hooks.YuniHook;
import dev.hcr.hcf.koths.commands.KothCommandManager;
import dev.hcr.hcf.koths.commands.player.DefaultKothCommand;
import dev.hcr.hcf.listeners.entities.ZombieListener;
import dev.hcr.hcf.listeners.factions.FactionClaimingListener;
import dev.hcr.hcf.listeners.factions.FactionListener;
import dev.hcr.hcf.listeners.factions.FactionTerritoryProtectionListener;
import dev.hcr.hcf.listeners.player.*;
import dev.hcr.hcf.pvpclass.tasks.KitDetectionTask;
import dev.hcr.hcf.pvpclass.types.ArcherClass;
import dev.hcr.hcf.pvpclass.types.RogueClass;
import dev.hcr.hcf.pvpclass.types.bard.BardClass;
import dev.hcr.hcf.pvpclass.types.MinerClass;
import dev.hcr.hcf.scoreboard.HCFBoardAdapter;
import dev.hcr.hcf.scoreboard.TeamManager;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.backend.ConfigFile;
import dev.hcr.hcf.utils.backend.ConfigurationType;
import dev.hcr.hcf.utils.backend.types.PropertiesConfiguration;
import io.github.thatkawaiisam.assemble.Assemble;
import io.github.thatkawaiisam.assemble.AssembleStyle;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class HCF extends JavaPlugin {
    private static HCF plugin;
    private final NumberFormat format = NumberFormat.getCurrencyInstance();
    private IStorage storage;
    private FactionRegenTask regenTask;
    private PluginHook core;
    private TeamManager teamManager;

    private final List<ConfigFile> files = new ArrayList<>();

    @Override
    public void onEnable() {
        plugin = this;
        loadConfigurationFiles();
        registerCommands();
        implementDatabases();
        loadHooks();
        loadFactions();
        loadPvPClasses();
        registerEvents();
        loadUI();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        saveFactions();
        // Runtime.getRuntime().exec("mongoexport --host host_name --port port_number --db myDatabase --collection Page --out Page.json");
        User.getUsers().forEach(user -> getStorage().appendUserDataSync(user.save()));
        Bukkit.getOnlinePlayers().forEach(player -> FactionClaimingListener.removeWand(player.getInventory()));
    }

    private void loadConfigurationFiles() {
        files.addAll(Arrays.asList(
                new ConfigFile("config", this)
        ));
        new PropertiesConfiguration("hcf.properties");

        new PropertiesConfiguration("database.properties", "database");
        new PropertiesConfiguration("mongo.properties", "database");

        new PropertiesConfiguration("faction.properties", "factions");
        new PropertiesConfiguration("claims.properties", "factions");
        new PropertiesConfiguration("pvpclass.properties", "factions");
    }

    public ConfigFile getConfiguration(String file) {
        return files.stream().filter(configFile -> configFile.getName().equalsIgnoreCase(file)).findAny().orElse(null);
    }

    private void registerCommands() {
        new FactionCommandManager();
        new KothCommandManager();
        new LivesCommandManager();
        getCommand("balance").setExecutor(new BalanceCommand());
        getCommand("claimbonuschest").setExecutor(new ClaimBonusChestCommand());
        getCommand("economy").setExecutor(new EconomyCommand());
        getCommand("glowstonescanner").setExecutor(new GlowstoneScannerCommand());
        getCommand("koth").setExecutor(new DefaultKothCommand());
        getCommand("faction").setExecutor(new DefaultFactionCommand());
        getCommand("lives").setExecutor(new DefaultLivesCommand());
        getCommand("pay").setExecutor(new PayCommand());
        getCommand("pvptimer").setExecutor(new PvPTimerCommand());
        getCommand("sotw").setExecutor(new SOTWCommand());
    }

    private void implementDatabases() {
        // To prevent NPE errors start the regen task before loading factions.
        regenTask = new FactionRegenTask();
        regenTask.runTaskTimerAsynchronously(this, 0L, 20L * PropertiesConfiguration.getPropertiesConfiguration("faction.properties").getInteger("regen-delay-time"));
        PropertiesConfiguration configuration = (PropertiesConfiguration) PropertiesConfiguration.getPropertiesConfiguration("database.properties");
        switch (configuration.getString("main-loader").toLowerCase()) {
            case "mongo":
            case "mongodb":
                storage = new MongoStorage();
                break;
            case "mysql":
            case "sql":
                getLogger().severe("Unsupported database provided.");
                Bukkit.shutdown();
                break;
            case "redis":
                getLogger().severe("Unsupported database provided.");
                Bukkit.shutdown();
                break;
        }
    }

    private void loadHooks() {
        if (new AquaCoreHook().isHooked()) {
            core = new AquaCoreHook();
        } else if (new YuniHook().isHooked()) {
            core = new YuniHook();
        } else {
            HCF.getPlugin().getLogger().severe("Could not find core hook. Please add support to a plugin or use a plugin that already has support.");
        }
    }

    private void loadFactions() {
        if (Faction.getWilderness() == null) {
            getLogger().warning("Wilderness not found, creating...");
            new WildernessFaction();
        }
        if (Faction.getWarzone() == null) {
            getLogger().warning("Warzone not found, creating...");
            new WarzoneFaction();
        }
        if (Faction.getSafeZone() == null) {
            getLogger().warning("SafeZone not found, creating...");
            new SafeZoneFaction();
        }
        if (Faction.getRoadFaction("north") == null) {
            getLogger().warning("North Road not found, creating...");
            new NorthRoad();
        }
        if (Faction.getRoadFaction("east") == null) {
            getLogger().warning("East Road not found, creating...");
            new EastRoad();
        }
        if (Faction.getRoadFaction("south") == null) {
            getLogger().warning("South Road not found, creating...");
            new SouthRoad();
        }
        if (Faction.getRoadFaction("west") == null) {
            getLogger().warning("West Road not found, creating...");
            new WestRoad();
        }
        if (Faction.getGlowStoneMountainFaction() == null) {
            getLogger().warning("Glowstone Mountain not found, creating...");
            new GlowStoneMountainFaction();
        }
    }

    private void loadPvPClasses() {
        KitDetectionTask detectionTask = new KitDetectionTask();
        detectionTask.runTaskTimerAsynchronously(this, 20L, 5L);
        new ArcherClass();
        new MinerClass();
        new BardClass();
        new RogueClass();
    }

    private void saveFactions() {
        for (Faction faction : Faction.getFactions()) {
            faction.save();
        }
    }

    private void registerEvents() {
        // Handle packet managers
        Arrays.asList(new UserListener(), new ChatListener(this), new FactionListener(), new FactionTerritoryProtectionListener(), new FactionClaimingListener(),
                new PlayerListener(), new GlassListener(), new TimerListener(), new MiningListener(), new PlayerPacketListener(), new HCFBoardAdapter(), new DeathBanListener(),
                new PvPClassListener(), new ZombieListener()
        ).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
    }

    private void loadUI() {
        Assemble assemble = new Assemble(this, new HCFBoardAdapter());
        assemble.setAssembleStyle(AssembleStyle.KOHI);
        teamManager = new TeamManager();
    }

    public FactionRegenTask getRegenTask() {
        return regenTask;
    }

    public NumberFormat getFormat() {
        return format;
    }

    public PluginHook getCore() {
        return core;
    }

    public IStorage getStorage() {
        return storage;
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public static HCF getPlugin() {
        return plugin;
    }
}
