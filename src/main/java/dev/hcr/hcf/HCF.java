package dev.hcr.hcf;

import dev.hcr.hcf.commands.admin.EconomyCommand;
import dev.hcr.hcf.commands.admin.SOTWCommand;
import dev.hcr.hcf.commands.donor.ClaimBonusChestCommand;
import dev.hcr.hcf.commands.players.BalanceCommand;
import dev.hcr.hcf.commands.players.PayCommand;
import dev.hcr.hcf.databases.MongoImplementation;
import dev.hcr.hcf.deathbans.DeathBan;
import dev.hcr.hcf.deathbans.DeathBanListener;
import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.commands.member.DefaultFactionCommand;
import dev.hcr.hcf.factions.commands.FactionCommandManager;
import dev.hcr.hcf.factions.structure.regen.FactionRegenTask;
import dev.hcr.hcf.factions.types.SafeZoneFaction;
import dev.hcr.hcf.factions.types.WarzoneFaction;
import dev.hcr.hcf.factions.types.WildernessFaction;
import dev.hcr.hcf.factions.types.roads.EastRoad;
import dev.hcr.hcf.factions.types.roads.NorthRoad;
import dev.hcr.hcf.factions.types.roads.SouthRoad;
import dev.hcr.hcf.factions.types.roads.WestRoad;
import dev.hcr.hcf.hooks.AquaCoreHook;
import dev.hcr.hcf.hooks.CoveHook;
import dev.hcr.hcf.hooks.PluginHook;
import dev.hcr.hcf.listeners.factions.FactionClaimingListener;
import dev.hcr.hcf.listeners.factions.FactionListener;
import dev.hcr.hcf.listeners.factions.FactionTerritoryProtectionListener;
import dev.hcr.hcf.listeners.player.*;
import dev.hcr.hcf.packets.PacketHandler;
import dev.hcr.hcf.pvpclass.tasks.KitDetectionTask;
import dev.hcr.hcf.pvpclass.tasks.PassiveEffectApplyTask;
import dev.hcr.hcf.pvpclass.types.ArcherClass;
import dev.hcr.hcf.pvpclass.types.BardClass;
import dev.hcr.hcf.pvpclass.types.MinerClass;
import dev.hcr.hcf.scoreboard.HCFBoardAdapter;
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
    private MongoImplementation database;
    private FactionRegenTask regenTask;
    private PacketHandler packetHandler;
    private PluginHook core;
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
        User.getUsers().forEach(user -> getMongoImplementation().appendUserDataSync(user.save()));
        Bukkit.getOnlinePlayers().forEach(player -> FactionClaimingListener.removeWand(player.getInventory()));
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
        getCommand("pay").setExecutor(new PayCommand());
        getCommand("sotw").setExecutor(new SOTWCommand());
    }

    private void implementDatabases() {
        database = new MongoImplementation(this);
        database.loadFactions();
        database.loadDeathBans();
    }

    private void loadHooks() {
        if (new AquaCoreHook().isHooked()) {
            core = new AquaCoreHook();
        } else if (new CoveHook().isHooked()) {
            core = new CoveHook();
        } else {
            HCF.getPlugin().getLogger().severe("Could not find core hook. Please add support to a plugin or use a plugin that already has support.");
        }
    }

    private void loadFactions() {
        regenTask = new FactionRegenTask();
        regenTask.runTaskTimerAsynchronously(this, 20L, 20L * ConfigurationType.getConfiguration("faction.properties").getInteger("regen-delay-time"));
        if (Faction.getWilderness() == null) {
            new WildernessFaction();
        }
        if (Faction.getWarzone() == null) {
            new WarzoneFaction();
        }
        if (Faction.getSafeZone() == null) {
            new SafeZoneFaction();
        }
        if (Faction.getRoadFaction("north") == null) {
            new NorthRoad();
        }
        if (Faction.getRoadFaction("east") == null) {
            new EastRoad();
        }
        if (Faction.getRoadFaction("south") == null) {
            new SouthRoad();
        }
        if (Faction.getRoadFaction("west") == null) {
            new WestRoad();
        }
    }

    private void loadPvPClasses() {
        ConfigFile config = getConfiguration("config");
        if (config.getBoolean("use-ihcf-events")) {
            Bukkit.getPluginManager().registerEvents(new iHCFListener(), this);
        } else {
            KitDetectionTask detectionTask = new KitDetectionTask();
            detectionTask.runTaskTimerAsynchronously(this, 20L, 5L);
            PassiveEffectApplyTask applyTask = new PassiveEffectApplyTask();
            applyTask.runTaskTimerAsynchronously(this, 20L, 5L);
        }
        new ArcherClass();
        new MinerClass();
        new BardClass();
    }

    private void saveFactions() {
        for (Faction faction : Faction.getFactions()) {
            faction.save();
        }
    }

    private void registerEvents() {
        // Handle packet managers
        packetHandler = new PacketHandler();
        Arrays.asList(new UserListener(), new ChatListener(this), new FactionListener(), new FactionTerritoryProtectionListener(), new FactionClaimingListener(),
                new PlayerListener(), new TimerListener(), new MiningListener(), new PlayerPacketListener(), new DeathBanListener(), new PvPClassListener()).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
    }

    private void loadUI() {
        Assemble assemble = new Assemble(this, new HCFBoardAdapter());
        assemble.setAssembleStyle(AssembleStyle.KOHI);
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

    public PluginHook getCore() {
        return core;
    }

    public static HCF getPlugin() {
        return plugin;
    }
}
