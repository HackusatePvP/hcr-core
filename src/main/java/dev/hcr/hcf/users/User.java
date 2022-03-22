package dev.hcr.hcf.users;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.listeners.factions.FactionClaimingListener;
import dev.hcr.hcf.pvpclass.PvPClass;
import dev.hcr.hcf.pvpclass.structure.Abilities;
import dev.hcr.hcf.timers.Timer;
import dev.hcr.hcf.timers.types.player.CombatTimer;
import dev.hcr.hcf.timers.types.player.EnderPearlTimer;
import dev.hcr.hcf.timers.types.player.effects.ArcherResistanceTimer;
import dev.hcr.hcf.timers.types.player.effects.ArcherSpeedTimer;
import dev.hcr.hcf.timers.types.player.faction.FactionHomeTimer;
import dev.hcr.hcf.users.faction.ChatChannel;
import dev.hcr.hcf.users.statistics.types.OreStatistics;
import dev.hcr.hcf.users.statistics.types.PvPStatistics;
import dev.hcr.hcf.utils.TaskUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class User {
    private final UUID uuid;
    private final String name;
    private OreStatistics oreStatistics;
    private PvPStatistics pvPStatistics;
    private double balance;
    private boolean claimedChest;
    private boolean factionMap = false;
    private boolean bypass = false;
    private PvPClass currentClass = null;
    private ChatChannel channel = ChatChannel.PUBLIC;
    private Faction faction;
    private final Set<Timer> activeTimers = new HashSet<>();

    private final static ConcurrentHashMap<UUID, User> users = new ConcurrentHashMap<>();

    public User(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.pvPStatistics = new PvPStatistics(this);
        this.oreStatistics = new OreStatistics(this);
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

    public void load(Document document) {
        this.pvPStatistics = new PvPStatistics(this, document);
        this.oreStatistics = new OreStatistics(this, document);
        if (document.containsKey("balance")) {
            this.balance = document.getDouble("balance");
        }
        if (document.containsKey("claimedChest")) {
            this.claimedChest = document.getBoolean("claimedChest");
        }
    }

    public Document save() {
        Document document = new Document("uuid", uuid.toString());
        document.append("name", this.name);
        oreStatistics.getStatisticKeyMapping().forEach(document::append);
        document.append("balance", balance);
        document.append("claimedChest", claimedChest);
        return document;
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

    public boolean hasFactionMap() {
        return factionMap;
    }

    public void setFactionMap(boolean factionMap) {
        this.factionMap = factionMap;
    }

    public boolean hasClaimedChest() {
        return claimedChest;
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

    public boolean inCombat() {
        return activeTimers.stream().filter(timer -> timer.getName().equalsIgnoreCase("combat")).findAny().orElse(null) == null;
    }

    public boolean canEnderPearl() {
        return activeTimers.stream().filter(timer -> timer.getName().equalsIgnoreCase("enderpearl")).findAny().orElse(null) == null;
    }


    public void setTimer(String timer, boolean add) {
        Player player = toPlayer();
        Timer currentTimer = activeTimers.stream().filter(timer1 -> timer1.getName().equalsIgnoreCase(timer)).findAny().orElse(null);
        if (add) {
            if (currentTimer != null) {
                currentTimer.end(true);
            }
            Timer.createTimerForPlayer(timer, player);
        } else {
            if (currentTimer == null) return;
            currentTimer.end(true);
            activeTimers.remove(currentTimer);
        }
    }

   /* public void setCombat(boolean combat) {
        Player player = toPlayer();
        CombatTimer timer = (CombatTimer) activeTimers.stream().filter(timer1 -> timer1.getName().equalsIgnoreCase("combat")).findAny().orElse(null);
        if (combat) {
            if (timer != null) {
                timer.end(true);
            }
            new CombatTimer(player);
        } else {
           if (timer == null) return;
           timer.end(true);
           activeTimers.remove(timer);
        }
    }

    public void setEnderPearl(boolean enderpearl) {
        Player player = toPlayer();
        EnderPearlTimer timer = (EnderPearlTimer) getActiveTimer("enderpearl");
        if (enderpearl) {
            if (timer == null) {
                new EnderPearlTimer(player);
            } else {
                timer.setDelay(30L);
                timer.setActive(true);
                try {
                    timer.runTaskTimerAsynchronously(HCF.getPlugin(), 20L, 20L);
                } catch (IllegalStateException ignored) {

                }
            }
        } else {
            if (timer == null) return;
            timer.end(true);
            activeTimers.remove(timer);
        }
    }

    public void setTimer(Timer timer, boolean set) {
        Player player = toPlayer();
        if (timer instanceof FactionHomeTimer) {
            FactionHomeTimer homeTimer = (FactionHomeTimer) getActiveTimer("faction_home");
            if (set) {
                if (homeTimer == null) {
                    new FactionHomeTimer(player);
                } else {
                    homeTimer.setDelay(30L);
                    homeTimer.setActive(true);
                    try {
                        homeTimer.runTaskTimerAsynchronously(HCF.getPlugin(), 20L, 20L);
                    } catch (IllegalStateException ignored) {

                    }
                }
            }
        }
    } */

    public void addEffectCooldown(Abilities ability) {
        Player player = toPlayer();
        Timer timer;
        switch (ability) {
            case ARCHER_SPEED:
                timer = getActiveTimer("archer_speed");
                if (timer == null) {
                    new ArcherSpeedTimer(player);
                } else {
                    timer.setDelay(ability.getCooldown());
                    timer.setActive(true);
                    try {
                        timer.runTaskTimerAsynchronously(HCF.getPlugin(), 20L, 20L);
                    } catch (IllegalStateException ignored) {

                    }
                }
                break;
            case ARCHER_RESISTANCE:
                timer = getActiveTimer("archer_resistance");
                if (timer == null) {
                    new ArcherResistanceTimer(player);
                } else {
                    timer.setDelay(ability.getCooldown());
                    timer.setActive(true);
                    try {
                        timer.runTaskTimerAsynchronously(HCF.getPlugin(), 20L, 20L);
                    } catch (IllegalStateException ignored) {

                    }
                }
                break;
        }
    }

    public boolean hasEffectCooldown(Abilities ability) {
        return getEffectCooldown(ability) != null;
    }

    public Timer getEffectCooldown(Abilities ability) {
        return activeTimers.stream().filter(timer1 -> timer1.getName().equalsIgnoreCase(ability.getName())).findAny().orElse(null);
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

    public Timer getActiveTimer(String name) {
        return activeTimers.stream().filter(timer -> timer != null && timer.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    public Set<Timer> getActiveTimers() {
        return activeTimers;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Player toPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public PvPStatistics getPvPStatistics() {
        return pvPStatistics;
    }

    public OreStatistics getOreStatistics() {
        return oreStatistics;
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
        HCF.getPlugin().getMongoImplementation().loadUserAsync(uuid, name);
        return users.get(uuid);
    }

    public static User getUser(String name) {
        return users.values().stream().filter(user -> user.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    public static Collection<User> getUsers() {
        return users.values();
    }

    public static User getUser(UUID uuid) {
        return users.get(uuid);
    }
}
