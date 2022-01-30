package dev.hcr.hcf.factions;


import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Faction {
    private final UUID uniqueID;
    private final String name;

    private static final ConcurrentHashMap<UUID, Faction> factionUUIDMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Faction> factionNameMap = new ConcurrentHashMap<>();


    public Faction(UUID uuid, String name) {
        this.name = name;
        factionNameMap.put(name.toLowerCase(), this);
        this.uniqueID = uuid;
        factionUUIDMap.put(uniqueID, this);
    }

    public static Collection<Faction> getFactions() {
        return factionUUIDMap.values();
    }

    public static Faction getFactionByName(String name) {
        return factionNameMap.get(name.toLowerCase());
    }

    public static Faction getByUniqueID(UUID uuid) {
        return factionUUIDMap.get(uuid);
    }

    public UUID getUniqueID() {
        return uniqueID;
    }

    public String getName() {
        return name;
    }

}
