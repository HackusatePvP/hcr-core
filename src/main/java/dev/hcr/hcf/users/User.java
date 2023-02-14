package dev.hcr.hcf.users;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.listeners.factions.FactionClaimingListener;
import dev.hcr.hcf.pvpclass.PvPClass;
import dev.hcr.hcf.pvpclass.structure.Abilities;
import dev.hcr.hcf.timers.Timer;
import dev.hcr.hcf.timers.events.TimerStartEvent;
import dev.hcr.hcf.timers.types.PauseTimer;
import dev.hcr.hcf.users.faction.ChatChannel;
import dev.hcr.hcf.users.statistics.UserStatistics;
import dev.hcr.hcf.utils.TaskUtils;
import dev.hcr.hcf.utils.backend.types.PropertiesConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class User {
    private final UUID uuid;
    private final String name;
    private double balance;
    private int lives;
    private boolean claimedChest;
    private boolean factionMap = false;
    private boolean bypass = false;
    private boolean sotw = false;
    private UserStatistics userStatistics;
    private PvPClass currentClass = null;
    private ChatChannel channel = ChatChannel.PUBLIC;
    private Faction faction;
    private final Set<Timer> activeTimers = new HashSet<>();

    private final static ConcurrentHashMap<UUID, User> users = new ConcurrentHashMap<>();
    private final static Map<UUID, BukkitTask> wallBorderTask = new HashMap<>();

    private final boolean debug = PropertiesConfiguration.getPropertiesConfiguration("hcf.properties").getBoolean("debug");

    public User(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        userStatistics = new UserStatistics(uuid);
        loadFaction();
        users.put(uuid, this);
    }

    private void loadFaction() {
        TaskUtils.runAsync(() -> {
            for (Faction faction : Faction.getFactions()) {
                if (!(faction instanceof PlayerFaction)) continue;
                if (((PlayerFaction) faction).hasMember(uuid)) {
                    this.faction = faction;
                    return;
                }
            }
        });
    }

    public void load(Map<String, Object> map) {

        if (map.containsKey("balance")) {
            this.balance = (Double) map.get("balance");
        }
        if (map.containsKey("claimedChest")) {
            this.claimedChest = (Boolean) map.get("claimedChest");
        }
        if (map.containsKey("statsMap")) {
            String statsString = (String) map.get("statsMap");

            Map<String, Object> statsMap = new HashMap<>();
            String[] entrySplit = statsString.split("%");
            for (String s : entrySplit) {
                String key = s.split("&")[0];
                String value = s.split("&")[1];
                try {
                    int i = Integer.parseInt(value);
                    statsMap.put(key, i);
                } catch (NumberFormatException ignored) {
                    statsMap.put(key, value);
                }
            }
            userStatistics = new UserStatistics(uuid, statsMap);
        }
    }

    public Map<String, Object> save() {
        Map<String, Object> map = new HashMap<>();
        map.put("uuid", uuid.toString());
        map.put("name", this.name);
        map.put("balance", this.balance);
        map.put("claimedChest", claimedChest);
        Map<String, Object> statsMap = new HashMap<>(userStatistics.toMap());

        StringBuilder builder = new StringBuilder();
        for (String s : statsMap.keySet()) {
            builder.append(s).append("&").append(statsMap.get(s)).append("%");
        }
        map.put("statsMap", builder.toString());
        if (debug) {
            System.out.println("Stats Map: " + builder);
        }
        return map;
    }

    public ChatChannel getChannel() {
        return channel;
    }

    public void setChannel(ChatChannel channel) {
        this.channel = channel;
    }

    public double getBalance() {
        return balance;
    }

    public void addToBalance(double amount) {
        balance += amount;
    }

    public void takeFromBalance(double amount) {
        balance -= amount;
    }

    public void setBalance(double amount) {
        balance = amount;
    }

    public Faction getFaction() {
        return faction;
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
    }

    public boolean hasFaction() {
        return faction != null;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public UserStatistics getUserStatistics() {
        return userStatistics;
    }

    public boolean hasFactionMap() {
        return factionMap;
    }

    public void setFactionMap(boolean factionMap) {
        this.factionMap = factionMap;
    }

    public boolean hasClaimedChest() {
        return claimedChest;
    }

    public void setClaimedChest(boolean claimedChest) {
        this.claimedChest = claimedChest;
    }

    public boolean isClaimingLand() {
        return FactionClaimingListener.claiming.containsKey(this);
    }

    public boolean hasBypass() {
        return bypass;
    }

    public void setBypass(boolean bypass) {
        this.bypass = bypass;
    }

    public void setTimer(String timer, boolean add) {
        Player player = toPlayer();
        Timer currentTimer = activeTimers.stream().filter(timer1 -> timer1.getName().equalsIgnoreCase(timer)).findAny().orElse(null);
        if (add) {
            if (currentTimer != null) {
                currentTimer.end(true);
            }
            Timer newTimer = Timer.createTimerForPlayer(timer, player);
            if (newTimer == null) return;
            TimerStartEvent event = new TimerStartEvent(newTimer);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                newTimer.end(true);
            }
        } else {
            if (currentTimer == null) return;
            currentTimer.end(true);
            activeTimers.remove(currentTimer);
            if (currentTimer instanceof PauseTimer) {
                HCF.getPlugin().getStorage().removeTimer((PauseTimer) currentTimer);
            }
        }
    }

    public void setTimer(Abilities type, boolean add) {
        System.out.println("Created timer for " + name);
        Player player = toPlayer();
        Timer currentTimer = activeTimers.stream().filter(timer1 -> timer1.getName().equalsIgnoreCase(type.getName())).findAny().orElse(null);
        if (add) {
            if (currentTimer != null) {
                if (debug) {
                    System.out.println(currentTimer.getName() + "timer ending...");
                }
                currentTimer.end(true);
            }
            Timer.createEffectTimer(player, type);
        } else {
            if (currentTimer == null) return;
            currentTimer.end(true);
            activeTimers.remove(currentTimer);
        }
    }

    public Set<Timer> getActiveTimers() {
        return activeTimers;
    }

    public Timer getActiveTimer(String timerName) {
        return activeTimers.stream().filter(timer -> timer.getName().equalsIgnoreCase(timerName) && timer.isActive()).findAny().orElse(null);
    }

    public Timer getActiveTimer(Abilities type) {
        return activeTimers.stream().filter(timer -> timer.getName().equalsIgnoreCase(type.getName()) && timer.isActive()).findAny().orElse(null);
    }

    public boolean hasTimer(String timerName) {
        return activeTimers.stream().filter(timer -> timer.getName().equalsIgnoreCase(timerName)).findAny().orElse(null) != null;
    }

    public boolean hasActiveTimer(String timerName) {
        Timer timer = activeTimers.stream().filter(timer1 -> timer1 != null && timer1.getName().equalsIgnoreCase(timerName)).findAny().orElse(null);
        if (timer != null) {
            return timer.isActive();
        }
        return false;
    }

    public boolean hasActiveTimer(Abilities type) {
        return activeTimers.stream().filter(timer1 -> timer1.getName().equalsIgnoreCase(type.getName()) && timer1.isActive()).findAny().orElse(null) != null;
    }

    public boolean hasAnyEffectCooldown() {
        for (Timer timer : activeTimers) {
            Abilities abilities =  Abilities.stream().filter(abilities1 -> abilities1.getName().equals(timer.getName())).findAny().orElse(null);
            return abilities != null;
        }
        return false;
    }

    public PvPClass getCurrentClass() {
        return currentClass;
    }

    public void setCurrentClass(PvPClass currentClass) {
        this.currentClass = currentClass;
    }

    public UUID getUniqueID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Player toPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public boolean hasSotw() {
        return sotw;
    }

    public void setSotw(boolean sotw) {
        this.sotw = sotw;
    }

    /**
     * Attempt to get a user form the cached mappings. Or attempt to unsafely load the user.
     * @param uuid - Of the {@link Player} or {@link OfflinePlayer}
     * @param name - Name of the user/player.
     * @return A new or already existent instance of the user.
     */
    public static User getUser(UUID uuid, String name) {
        if (users.containsKey(uuid)) {
            return users.get(uuid);
        }
        HCF.getPlugin().getStorage().loadUserAsync(uuid, name);
        return users.get(uuid);
    }

    public static User getUser(String name) {
        return users.values().stream().filter(user -> user.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    public static Collection<User> getUsers() {
        return users.values();
    }

    public static User getUser(UUID uuid) {
        if (!users.containsKey(uuid)) {
            // The mapping does not contain the uuid attempt to load the user
            HCF.getPlugin().getStorage().loadUserAsync(uuid);
            return users.get(uuid);
        }
        return users.get(uuid);
    }

    public static User getOfflineUser(String name) {
        Player target = Bukkit.getPlayer(name);
        UUID targetUUID;
        if (target == null) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
            if (offlinePlayer == null) {
                return null;
            }
            targetUUID = offlinePlayer.getUniqueId();
        } else {
            targetUUID = target.getUniqueId();
        }
        User user = User.getUser(targetUUID);;
        return user;
    }

    public static Map<UUID, BukkitTask> getWallBorderTask() {
        return wallBorderTask;
    }
}
