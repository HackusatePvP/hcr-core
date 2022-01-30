package dev.hcr.hcf.factions.types;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.users.faction.Role;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerFaction extends Faction {
    private final UUID leader;
    private double balance;
    private int points;
    private final Map<String, UUID> factionInviteMap = new HashMap<>();
    private final Collection<UUID> factionMembers = new HashSet<>();
    private final Map<UUID, Role> roleMap = new HashMap<>();

    public PlayerFaction(String name, UUID leader) {
        super(UUID.randomUUID(), name);
        this.leader = leader;
        User user = User.getUser(leader);
        user.setFaction(this);
        roleMap.put(leader, Role.LEADER);
    }

    public UUID getLeader() {
        return leader;
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

    public void addUserToFaction(User user) {
        factionMembers.add(user.getUuid());
        roleMap.put(user.getUuid(), Role.MEMBER);
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
        // TODO: 1/29/2022 send faction message to log invite

        if (user.toPlayer() != null) {
            Player player = user.toPlayer();
            player.sendMessage("&aYou have been invited to &b" + getName() + "&a. /f join <" + getName() + ">");
        }
        return true;
    }

    public boolean hasInvite(Player player) {
        return factionInviteMap.containsKey(player.getName());
    }
}
