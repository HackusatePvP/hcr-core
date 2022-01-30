package dev.hcr.hcf.users;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.users.faction.ChatChannel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class User {
    private final UUID uuid;
    private final String name;
    private ChatChannel channel = ChatChannel.PUBLIC;
    private Faction faction;

    private final static ConcurrentHashMap<UUID, User> users = new ConcurrentHashMap<>();

    public User(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.faction = null;
        users.put(uuid, this);
    }

    public ChatChannel getChannel() {
        return channel;
    }

    public void setChannel(ChatChannel channel) {
        this.channel = channel;
    }

    public Faction getFaction() {
        return faction;
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public static User getUser(UUID uuid) {
        return users.get(uuid);
    }

    public Player toPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public static User getUser(String name) {
        return users.values().stream().filter(user -> user.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }
}
