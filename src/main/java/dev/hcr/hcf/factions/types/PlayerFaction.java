package dev.hcr.hcf.factions.types;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.claims.Claim;
import dev.hcr.hcf.factions.events.members.PlayerFactionLeaveEvent;
import dev.hcr.hcf.factions.events.members.PlayerJoinFactionEvent;
import dev.hcr.hcf.factions.structure.regen.FactionRegenTask;
import dev.hcr.hcf.factions.structure.regen.RegenStatus;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.users.faction.Role;
import dev.hcr.hcf.utils.CC;
import dev.hcr.hcf.utils.backend.types.PropertiesConfiguration;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.text.DecimalFormat;
import java.util.*;

public class PlayerFaction extends Faction {
    private UUID leader;
    private double balance;
    private int points;
    private double currentDTR, maxDTR;
    private RegenStatus regenStatus = RegenStatus.FULL;
    private final Map<String, UUID> factionInviteMap = new HashMap<>();
    private final Collection<UUID> factionMembers = new HashSet<>();
    private final Map<UUID, Role> roleMap = new HashMap<>();
    private final Collection<UUID> factionAllies = new HashSet<>();
    private final Collection<UUID> pendingAllies = new HashSet<>();

    private long regenDelayTime;

    // Scoreboard trackers cause why not
    private final Scoreboard scoreboard;

    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private final HCF plugin = HCF.getPlugin();

    // Debug switch
    private final boolean debug = PropertiesConfiguration.getPropertiesConfiguration("hcf.properties").getBoolean("debug");
    // Kitmap boolean
    private final boolean kitmap = PropertiesConfiguration.getPropertiesConfiguration("hcf.properties").getBoolean("kitmap");

    public PlayerFaction(String name, UUID leader) {
        super(UUID.randomUUID(), name, new Claim(name), true);
        this.leader = leader;
        this.currentDTR = 1.1;
        this.maxDTR = 1.1;
        User user = User.getUser(leader);
        factionMembers.add(leader);
        user.setFaction(this);
        roleMap.put(leader, Role.LEADER);
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        scoreboard.registerNewTeam(name);
    }

    public PlayerFaction(Map<String, Object> map) {
        super(map);
        this.leader = UUID.fromString((String) map.get("leader"));
        load(map);
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        scoreboard.registerNewTeam("friendly");
        scoreboard.registerNewTeam("enemy");
    }

    @Override
    public void load(Map<String, Object> map) {
        if (map.containsKey("balance")) {
            this.balance = (Double) map.get("balance");
        }
        if (map.containsKey("points")) {
            this.points = (Integer) map.get("points");
        }
        if (map.containsKey("members")) {
            List<String> members = (List<String>) map.get("members");
            for (String s : members) {
                factionMembers.add(UUID.fromString(s));
            }
        }
        if (map.containsKey("roles")) {
            List<String> roles = (List<String>) map.get("roles");
            for (String s : roles) {
                // Some crackhead loading
                // Roles are appended as String that contains both uuid and role
                // Separate the uuid and role with a single character "-"
                for (int i = 0; i < s.length(); i++) {
                    // Setup a counter that finds the "-" in the string
                    if (s.charAt(i) == '-') {
                        // Once found create a substring at the "-" which cuts off the beginning of the string
                        // Original: "uuid-role"
                        // Substring: -role

                        // Remove the - character
                        String sub = s.substring(i).replace("-", "");
                        System.out.println("Loading MEMBER: " + sub);
                        // Left with just the role which can get the Enum Role from the string using value of.

                        // Since the uuid isn't saved in hexadecimal format (no dashes) must add them before to get the UUID object.
                        String uuidToHexString = sub.replaceAll(
                                "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                                "$1-$2-$3-$4-$5");
                        UUID uuid = UUID.fromString(uuidToHexString);
                        s = s.replace("-", "");
                        // Take everything after the - and remove it to get the uuid.
                        s = s.replace(sub, "");
                        // Get the UUID object from the string
                        Role role = Role.valueOf(s);
                        // Insert data into map.
                        roleMap.put(uuid, role);
                    }
                }
            }
        }
        if (map.containsKey("currentDTR")) {
            this.maxDTR = getMaxDTR();
            this.currentDTR = (Double) map.get("currentDTR");
            // Check if the faction is suppose to be on regen.
            if (currentDTR < maxDTR) {
                System.out.println("Passed!");
                if (map.containsKey("regenDelayTime")) {
                    this.regenDelayTime = (Long) map.get("regenDelayTime");
                    loadRegenTask(false);
                } else {
                    loadRegenTask(true);
                }
            }
        }

        if (map.containsKey("allies")) {
            // Allies - allyuuid@allyuuid@allyuuid
            String allyString = (String) map.get("allies");
            if (!allyString.isEmpty()) {
                for (String uuid : allyString.split("@")) {
                    if (debug) {
                        System.out.println("Ally: " + uuid);
                    }
                    factionAllies.add(UUID.fromString(uuid));
                }
            }
        }

        // Call the super method for loading claims and other basic faction data.
        super.load(map);
    }

    @Override
    public double getDTRMultiplier() {
        return 1;
    }

    @Override
    public void save() {
        Document document = new Document("uuid", getUniqueID().toString());
        document.append("name", this.getName());
        document.append("leader", this.leader.toString());
        document.append("balance", this.balance);
        document.append("points", this.points);
        document.append("currentDTR", this.currentDTR);
        document.append("members", new ArrayList<>(factionMembers));
        ArrayList<String> roles = new ArrayList<>();
        roleMap.forEach((uuid, role) -> {
            roles.add(role.name() + "-" + uuid.toString());
        });
        document.append("roles", roles);
        ArrayList<String> invites = new ArrayList<>();
        factionInviteMap.forEach((s, uuid) -> invites.add(s + "*" + uuid.toString()));
        document.append("invites", invites);
        if (FactionRegenTask.isWaitingRegeneration(this)) {
            this.regenDelayTime = FactionRegenTask.getRegenPausedDelay(this);
            document.append("regenDelayTime", regenDelayTime);
        }

        StringBuilder builder = new StringBuilder();
        for (UUID uuid : factionAllies) {
            builder.append(uuid.toString()).append("@");
        }
        document.append("allies", builder.toString());
        plugin.getStorage().appendFactionData(document);
        super.save();
    }

    public UUID getLeader() {
        return leader;
    }

    public void setLeader(UUID currentOwner, UUID newOwner) {
        this.leader = newOwner;
        setRole(currentOwner, Role.MEMBER);
        setRole(newOwner, Role.LEADER);
        broadcast(CC.translate("&7[&4" + getName().toUpperCase() + "&7] &c" + User.getUser(currentOwner).getName() + " &7has transferred &eOWNERSHIP &7to &6" + User.getUser(newOwner).getName()));
    }

    public double getMaxDTR() {
        double dtr = 1.1 * factionMembers.size();
        if (dtr > PropertiesConfiguration.getPropertiesConfiguration("faction.properties").getDouble("max-dtr")) {
            return 5.5;
        }
        return dtr;
    }

    public double getCurrentDTR() {
        return currentDTR;
    }

    public void setCurrentDTR(double dtr) {
        this.currentDTR = dtr;
    }

    public void decreaseDTR(double amount) {
        // In kitmap factions will not be able to lose dtr
        if (kitmap) {
            return;
        }
        this.currentDTR -= amount;
        // Setup regen task
        this.regenStatus = RegenStatus.PAUSED;
        plugin.getRegenTask().setupFactionRegen(this);
    }

    private void loadRegenTask(boolean instant) {
        // If kitmap is enabled cancel the regen task. In kitmap factions will not lose dtr
        if (kitmap) {
            return;
        }

        if (debug) {
            System.out.println(getName() + " is regenerating...");
        }
        if (instant) {
            if (debug) {
                System.out.println("True!");
            }
            this.regenStatus = RegenStatus.REGENERATING;
            plugin.getRegenTask().instantRegen(this);
        } else {
            if (debug) {
                System.out.println("False!");
            }
            this.regenStatus = RegenStatus.PAUSED;
            FactionRegenTask.getFactionPausedRegenCooldown().put(this, regenDelayTime);
        }
    }

    public void increaseDTR(double amount) {
        this.currentDTR += amount;
    }

    public String getFormattedMaxDTR() {
        return decimalFormat.format(getMaxDTR());
    }

    public String getFormattedCurrentDTR() {
        return regenStatus.getColor() + regenStatus.getUnicode() + " " + decimalFormat.format(currentDTR);
    }

    public String getRaidableCurrentDTR() {
        return ChatColor.RED + regenStatus.getUnicode() + " " + decimalFormat.format(currentDTR);
    }

    public boolean isRaidable() {
        if (kitmap) {
            return false;
        }
        return getCurrentDTR() < 0;
    }

    public double getBalance() {
        return balance;
    }

    public void addToBalance(double amount) {
        this.balance += amount;
    }

    public void removeFromBalance(double amount) {
        this.balance -= amount;
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public void removePoints(int points) {
        this.points -= points;
    }

    public Role getRole(User user) {
        if (user.getFaction() == null || !user.getFaction().getName().equalsIgnoreCase(getName())) {
            return Role.NONE;
        }
        return roleMap.get(user.getUniqueID());
    }

    public Role getRole(UUID uuid) {
        return roleMap.get(uuid);
    }

    public void setRole(User user, Role role) {
        if (user.getFaction() == null || !user.getFaction().getName().equalsIgnoreCase(getName())) {
            return;
        }
        roleMap.put(user.getUniqueID(), role);
    }

    public void setRole(UUID uuid, Role role) {
        if (!this.hasMember(uuid)) {
            return;
        }
        roleMap.put(uuid, role);
    }

    public void depositBalance(double balance) {
        this.balance += balance;
    }

    public void withdrawBalance(double balance) {
        this.balance -= balance;
    }

    public boolean addUserToFaction(User user) {
        PlayerJoinFactionEvent event = new PlayerJoinFactionEvent(this, user.toPlayer());
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        factionMembers.add(user.getUniqueID());
        roleMap.put(user.getUniqueID(), Role.MEMBER);
        user.setFaction(this);

        if (user.toPlayer() != null) {
            HCF.getPlugin().getTeamManager().loadPlayer(user.toPlayer());
        }

        return true;
    }

    public boolean removeUserFromFaction(User user, boolean forced) {
        if (!forced) {
            PlayerFactionLeaveEvent event = new PlayerFactionLeaveEvent(this, user.toPlayer());
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return false;
            }
        }
        factionMembers.remove(user.getUniqueID());
        roleMap.remove(user.getUniqueID());
        user.setFaction(null);

        if (user.toPlayer() != null) {
            HCF.getPlugin().getTeamManager().loadPlayer(user.toPlayer());
        }
        return true;
    }

    public boolean hasMember(UUID uuid) {
        return factionMembers.contains(uuid);
    }

    public User getMember(UUID uuid) {
        return User.getUser(factionMembers.stream().filter(uuid1 -> uuid1 == uuid).findAny().orElse(null));
    }

    public Collection<UUID> getFactionMembers() {
        return factionMembers;
    }

    public Collection<Player> getOnlineMembers() {
        Collection<Player> members = new HashSet<>();
        for (UUID uuid : factionMembers) {
            Player member = Bukkit.getPlayer(uuid);
            if (member == null) continue;
            members.add(member);
        }
        return members;
    }

    public int getTotalOnlineMembers() {
        return getOnlineMembers().size();
    }

    public void promoteUser(Player player, User user) {
        if (getRole(user) == Role.MEMBER) {
            setRole(user, Role.CAPTAIN);
        } else if (getRole(user) == Role.CAPTAIN) {
            setRole(user, Role.COLEADER);
        }
        broadcast(CC.translate("&7[&4" + getName().toUpperCase() + "&7] &c" + player.getName() + " &7has &aPROMOTED &e" + user.getName() + " &7to &6" + getRole(user).name()));
    }

    public void demoteUser(Player player, User user) {
        if (getRole(user) == Role.COLEADER) {
            setRole(user, Role.CAPTAIN);
        } else if (getRole(user) == Role.CAPTAIN) {
            setRole(user, Role.MEMBER);
        }
        broadcast(CC.translate("&7[&4" + getName().toUpperCase() + "&7] &c" + player.getName() + " &7has &cDEMOTED &e" + user.getName() + " &7to &6" + getRole(user).name()));
    }

    public boolean sendInvite(Player inviter, User user) {
        factionInviteMap.put(user.getName(), inviter.getUniqueId());
        broadcast(CC.translate("&7[&4" + getName().toUpperCase() + "&7] &c" + inviter.getName() + " &7has invited &e" + user.getName() + " &7to the faction!"));

        if (user.toPlayer() != null) {
            Player player = user.toPlayer();
            TextComponent part1 = new TextComponent("You have been invited to ");
            part1.setColor(ChatColor.GREEN);
            TextComponent part2 = new TextComponent(getName() + " ");
            part2.setColor(ChatColor.AQUA);
            TextComponent part3 = new TextComponent("(/f join <" + getName() + ">)");
            part3.setColor(ChatColor.GRAY);
            part3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to join faction").color(ChatColor.LIGHT_PURPLE).create()));
            part3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f join " + getName()));
            player.spigot().sendMessage(part1, part2, part3);
        }
        return true;
    }

    public boolean hasInvite(Player player) {
        return factionInviteMap.containsKey(player.getName());
    }

    public void broadcast(String message) {
        for (Player player : getOnlineMembers()) {
            player.sendMessage(CC.translate(message));
        }
    }

    public RegenStatus getRegenStatus() {
        return regenStatus;
    }

    public void setRegenStatus(RegenStatus status) {
        this.regenStatus = status;
    }

    public long getRegenDelayTime() {
        return regenDelayTime;
    }

    public void setRegenDelayTime(long regenDelayTime) {
        this.regenDelayTime = regenDelayTime;
    }

    public Collection<UUID> getFactionAllies() {
        return factionAllies;
    }

    public void addAlly(PlayerFaction faction) {
        broadcast("&7[&4" + getName() + "&7] &7You are now allies with: &c" + faction.getName());
        HCF.getPlugin().getTeamManager().setAlly(this, faction, true);
        factionAllies.add(faction.getUniqueID());
    }

    public void removeAlly(PlayerFaction faction) {
        HCF.getPlugin().getTeamManager().setAlly(this, faction, false);
        factionAllies.remove(faction.getUniqueID());
    }

    public boolean hasAlly(Faction faction) {
        return hasAlly(faction.getUniqueID());
    }

    public boolean hasAlly(UUID uuid) {
        return factionAllies.contains(uuid);
    }

    public void addPendingAlly(UUID uuid) {
        addPendingAlly(Faction.getByUniqueID(uuid));
    }

    public void addPendingAlly(Faction faction) {
        broadcast("&7[&4" + getName() + "&7] &c" + faction.getName() + " &7wishes to be allied with you. Co-Leaders and above can &e'/f accept " + faction.getName() + "' &7to accept the alliegence.");
        pendingAllies.add(faction.getUniqueID());
    }

    public void removePendingAlly(Faction faction) {
        pendingAllies.remove(faction.getUniqueID());
    }

    public boolean hasPendingAlly(UUID uuid) {
        return pendingAllies.contains(uuid);
    }
    public boolean hasPendingAlly(Faction faction) {
        return pendingAllies.contains(faction.getUniqueID());
    }


    public Scoreboard getScoreboard() {
        return scoreboard;
    }
}
