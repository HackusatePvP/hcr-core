package dev.hcr.hcf.factions.types;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.claims.Claim;
import dev.hcr.hcf.factions.events.members.PlayerJoinFactionEvent;
import dev.hcr.hcf.factions.structure.regen.RegenStatus;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.users.faction.Role;
import dev.hcr.hcf.utils.CC;
import dev.hcr.hcf.utils.backend.ConfigurationType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;

public class PlayerFaction extends Faction {
    private final UUID leader;
    private double balance;
    private int points;
    private double currentDTR, maxDTR;
    private RegenStatus regenStatus = RegenStatus.FULL;
    private final Map<String, UUID> factionInviteMap = new HashMap<>();
    private final Collection<UUID> factionMembers = new HashSet<>();
    private final Map<UUID, Role> roleMap = new HashMap<>();

    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private final HCF plugin = HCF.getPlugin();

    public PlayerFaction(String name, UUID leader) {
        super(UUID.randomUUID(), name, new Claim(name));
        this.leader = leader;
        this.currentDTR = 1.1;
        this.maxDTR = 1.1;
        User user = User.getUser(leader);
        factionMembers.add(leader);
        user.setFaction(this);
        roleMap.put(leader, Role.LEADER);
    }

    public PlayerFaction(Document document) {
        super(document);
        this.leader = UUID.fromString(document.getString("leader"));
        load(document);
    }

    @Override
    public void load(Document document) {
        if (document.containsKey("balance")) {
            this.balance = document.getDouble("balance");
        }
        if (document.containsKey("points")) {
            this.points = document.getInteger("points");
        }
        if (document.containsKey("currentDTR")) {
            this.maxDTR = getMaxDTR();
            this.currentDTR = document.getDouble("currentDTR");
            // Check if the faction is suppose to be on regen.
            if (currentDTR < maxDTR) {
                loadRegenTask();
            }
        }
        if (document.containsKey("members")) {
            List<UUID> members = document.get("members", ArrayList.class);
            factionMembers.addAll(members);
        }
        if (document.containsKey("roles")) {
            List<String> roles = document.get("roles", ArrayList.class);
            for (String s : roles) {
                // Some crackhead loading
                // Roles are appended as String that contains both uuid and role
                // We separate the uuid and role with a single character "-"
                for (int i = 0; i < s.length(); i++) {
                    // Setup a counter that finds the "-" in the string
                    if (s.charAt(i) == '-') {
                        // Once found lets create a substring at the - which cuts off the beginning of the string
                        // Original: "uuid-role"
                        // Substring: -role

                        // Remove the - character
                        String sub = s.substring(i).replace("-", "");
                        System.out.println("Loading MEMBER: " + sub);
                        // Now we are left with just the role which we can get the Enum Role from the string using value of.

                        // Since the uuid isn't saved in hexadecimal format (no dashes) we must add them before we can get the UUID object.
                        String uuidToHexString = sub.replaceAll(
                                "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                                "$1-$2-$3-$4-$5");
                        UUID uuid = UUID.fromString(uuidToHexString);
                        s = s.replace("-", "");
                        // Now we have to get the uuid.
                        // To do this we will take everything after the - and remove it. Since we already have everything as a string "sub"
                        // its a simple replace statement.
                        s = s.replace(sub, "");
                        // Get the UUID object from the string
                        System.out.println("Loading role: " + s);
                        Role role = Role.valueOf(s);
                        // Insert data into map.
                        roleMap.put(uuid, role);
                    }
                }
            }
        }
        if (document.containsKey("invites")) {
            List<String> invites = document.get("invites", ArrayList.class);
            for (String s : invites) {
                for (int i = 0; i < s.length(); i++) {
                    if (s.charAt(i) == '-') {
                        String sub = s.substring(i).replace("*", "");
                        UUID inviter = UUID.fromString(sub);
                        s = s.replace(sub, "");
                        String invited = s;
                        factionInviteMap.put(s, inviter);
                    }
                }
            }
        }
        // Call the super method for loading claims and other basic faction data.
        super.load(document);
    }

    @Override
    public void save() {
        Document document = new Document("uuid", getUniqueID().toString());
        document.append("name", this.getName());
        document.append("leader", this.leader.toString());
        document.append("balance", this.balance);
        document.append("points", this.points);
        document.append("currentDTR", this.currentDTR);
        document.append("members", new ArrayList<UUID>(factionMembers));
        ArrayList<String> roles = new ArrayList<>();
        roleMap.forEach((uuid, role) -> {
            System.out.println("Adding " + uuid.toString() + ": " + role.name() + " To Document!");
            roles.add(role.name() + "-" + uuid.toString());
        });
        document.append("roles", roles);
        ArrayList<String> invites = new ArrayList<>();
        factionInviteMap.forEach((s, uuid) -> invites.add(s + "*" + uuid.toString()));
        document.append("invites", invites);

        //FIXME Duplicate code, I need to improve the faction system in order to call the super method "save".
        plugin.getMongoImplementation().appendFactionData(document);
        super.save();
    }

    public UUID getLeader() {
        return leader;
    }

    public double getMaxDTR() {
        double dtr = 1.1 * factionMembers.size();
        if (dtr > ConfigurationType.getConfiguration("faction.properties").getDouble("max-dtr")) {
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
        this.currentDTR -= amount;
        // Setup regen task
        this.regenStatus = RegenStatus.PAUSED;
        plugin.getRegenTask().setupFactionRegen(this);
    }

    private void loadRegenTask() {
        this.regenStatus = RegenStatus.REGENERATING;
        plugin.getRegenTask().instantRegen(this);
    }

    public void increaseDTR(double amount) {
        this.currentDTR += amount;
    }

    public String getFormattedMaxDTR() {
        return decimalFormat.format(getMaxDTR());
    }

    public String getFormattedCurrentDTR() {
        // TODO: 2/6/2022 If the faction is raidable set dtr string color to red
        return regenStatus.getColor() + regenStatus.getUnicode() + " " + decimalFormat.format(currentDTR);
    }

    public boolean isRaidable() {
        return getCurrentDTR() > 0;
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
        return roleMap.get(user.getUuid());
    }

    public void setRole(User user, Role role) {
        if (user.getFaction() == null || !user.getFaction().getName().equalsIgnoreCase(getName())) {
            return;
        }
        roleMap.put(user.getUuid(), role);
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
        factionMembers.add(user.getUuid());
        roleMap.put(user.getUuid(), Role.MEMBER);
        user.setFaction(this);
        return true;
    }

    public boolean removeUserFromFaction(User user) {
        // TODO: 2/5/2022 Add custom event
        factionMembers.remove(user.getUuid());
        roleMap.remove(user.getUuid());
        user.setFaction(null);
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

    public void promoteUser(User user) {
        if (getRole(user) == Role.MEMBER) {
            setRole(user, Role.CAPTAIN);
        } else if (getRole(user) == Role.CAPTAIN) {
            setRole(user, Role.COLEADER);
        }
    }

    public void demoteUser(User user) {
        if (getRole(user) == Role.COLEADER) {
            setRole(user, Role.CAPTAIN);
        } else if (getRole(user) == Role.CAPTAIN) {
            setRole(user, Role.MEMBER);
        }
    }

    public boolean sendInvite(Player inviter, User user) {
        factionInviteMap.put(user.getName(), inviter.getUniqueId());
        broadcast(CC.translate("&7[&4" + getName().toUpperCase() + "&7] &c" + inviter.getName() + " &7has invited &e" + user.getName() + " &7to the faction!"));

        if (user.toPlayer() != null) {
            Player player = user.toPlayer();
            // TODO: 1/30/2022 Make the message clickable to execute the join  command
            // player.sendMessage(CC.translate("&aYou have been invited to &b" + getName() + "&a. /f join <" + getName() + ">"));
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

    public void setRegenStatus(RegenStatus status) {
        this.regenStatus = status;
    }
}
