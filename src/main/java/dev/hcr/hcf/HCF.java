package dev.hcr.hcf;

import dev.hcr.hcf.commands.admin.EconomyCommand;
import dev.hcr.hcf.commands.admin.IamBardCommand;
import dev.hcr.hcf.commands.admin.KillAllCommand;
import dev.hcr.hcf.commands.donor.ClaimBonusChestCommand;
import dev.hcr.hcf.commands.players.BalanceCommand;
import dev.hcr.hcf.commands.players.PayCommand;
import dev.hcr.hcf.commands.players.PvPTimerCommand;
import dev.hcr.hcf.commands.players.lives.DefaultLivesCommand;
import dev.hcr.hcf.commands.players.lives.LivesCommandManager;
import dev.hcr.hcf.commands.players.sotw.SOTWCommandManager;
import dev.hcr.hcf.commands.players.sotw.players.DefaultSOTWCommand;
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
import dev.hcr.hcf.koths.KothListener;
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
import dev.hcr.hcf.scoreboard.ScoreboardListener;
import dev.hcr.hcf.scoreboard.TeamManager;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.users.UserSaveTask;
import dev.hcr.hcf.utils.backend.ItemDatabase;
import dev.hcr.hcf.utils.backend.types.PropertiesConfiguration;
import io.github.thatkawaiisam.assemble.Assemble;
import io.github.thatkawaiisam.assemble.AssembleStyle;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.NumberFormat;
import java.util.Arrays;

public final class HCF extends JavaPlugin {
    private static HCF plugin;
    private final NumberFormat format = NumberFormat.getCurrencyInstance();
    private IStorage storage;
    private FactionRegenTask regenTask;
    private PluginHook core;
    private TeamManager teamManager;

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
        getLogger().info(plugin.getDescription().getVersion() + " finished loading.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (storage != null) {
            storage.saveUsers();
            saveFactions();
        }

        User.getUsers().forEach(user -> getStorage().appendUserDataSync(user.save()));
        Bukkit.getOnlinePlayers().forEach(player -> FactionClaimingListener.removeWand(player.getInventory()));

        plugin = null;
    }

    private void loadConfigurationFiles() {
        getLogger().info("Loading configuration files...");
        new PropertiesConfiguration("hcf.properties");

        new PropertiesConfiguration("database.properties", "database");
        new PropertiesConfiguration("mongo.properties", "database");

        new PropertiesConfiguration("faction.properties", "factions");
        new PropertiesConfiguration("claims.properties", "factions");
        new PropertiesConfiguration("pvpclass.properties", "factions");

        new PropertiesConfiguration("deathbans.properties", "game");

        new ItemDatabase();

        if (PropertiesConfiguration.canUpdate()) {
            getLogger().warning("New update: Updating configuration files. (Note: This will not overwrite or reset any configuration you have altered.)");
            PropertiesConfiguration.update();
        }
    }


    private void registerCommands() {
        getLogger().info("Registering all commands...");
        new FactionCommandManager();
        new KothCommandManager();
        new LivesCommandManager();
        new SOTWCommandManager();
        getCommand("balance").setExecutor(new BalanceCommand());
        getCommand("claimbonuschest").setExecutor(new ClaimBonusChestCommand());
        getCommand("economy").setExecutor(new EconomyCommand());
        getCommand("glowstonescanner").setExecutor(new GlowstoneScannerCommand());
        getCommand("iambard").setExecutor(new IamBardCommand());
        getCommand("killall").setExecutor(new KillAllCommand());
        getCommand("koth").setExecutor(new DefaultKothCommand());
        getCommand("faction").setExecutor(new DefaultFactionCommand());
        getCommand("lives").setExecutor(new DefaultLivesCommand());
        getCommand("pay").setExecutor(new PayCommand());
        getCommand("pvptimer").setExecutor(new PvPTimerCommand());
        getCommand("sotw").setExecutor(new DefaultSOTWCommand());
    }

    private void implementDatabases() {
        getLogger().info("Connecting to database...");
        // To prevent NPE errors start the regen task before loading factions.
        regenTask = new FactionRegenTask();
        regenTask.runTaskTimerAsynchronously(this, 0L, 20L * PropertiesConfiguration.getPropertiesConfiguration("faction.properties").getLong("regen-delay-time"));

        PropertiesConfiguration configuration = PropertiesConfiguration.getPropertiesConfiguration("database.properties");
        getLogger().info("Loader: " + configuration.getString("main-loader").toLowerCase());

        switch (configuration.getString("main-loader").toLowerCase()) {
            case "mongo":
            case "mongodb":
                getLogger().info("MongoDB connection started.");
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
            default:
                storage = new MongoStorage();
        }

        UserSaveTask saveTask = new UserSaveTask();
        saveTask.runTaskTimerAsynchronously(this, 0L, 20L * PropertiesConfiguration.getPropertiesConfiguration("hcf.properties").getLong("user-autosave-delay"));

    }

    private void loadHooks() {
        getLogger().info("Looking for plugin hooks...");
        if (new AquaCoreHook().isHooked()) {
            getLogger().info("AquaCore hook found.");
            core = new AquaCoreHook();
        } else if (new YuniHook().isHooked()) {
            getLogger().info("Yuni hook found.");
            core = new YuniHook();
        } else {
            getLogger().severe("Could not find core hook. Please add support to a plugin or use a plugin that already has support.");
        }
    }

    private void loadFactions() {
        getLogger().info("Validating system factions...");
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
        getLogger().info("Registering PvP Classes...");
        KitDetectionTask detectionTask = new KitDetectionTask();
        detectionTask.runTaskTimerAsynchronously(this, 20L, 5L);
        new ArcherClass();
        new MinerClass();
        new BardClass();
        new RogueClass();
    }

    private void saveFactions() {
        getLogger().info("Saving all faction data...");
        for (Faction faction : Faction.getFactions()) {
            faction.save();
        }
    }

    private void registerEvents() {
        getLogger().info("Registering all listeners...");
        Arrays.asList(new UserListener(), new ChatListener(this), new FactionListener(), new FactionTerritoryProtectionListener(), new FactionClaimingListener(),
                new PlayerListener(), new GlassListener(), new MiningListener(), new PlayerPacketListener(),
                new PvPClassListener(), new ZombieListener(), new KothListener(), new SignListener(), new PortalListener(), new ScoreboardListener()
        ).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
        // Load kitmap stuff separately
        if (PropertiesConfiguration.getPropertiesConfiguration("hcf.properties").getBoolean("kitmap")) {
            // Kitmap is on

        } else {
            Bukkit.getPluginManager().registerEvents(new DeathBanListener(), this);
        }
    }

    private void loadUI() {
        getLogger().info("Loading scoreboard and tab...");
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
