package dev.hcr.hcf.factions;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.factions.claims.Claim;
import dev.hcr.hcf.factions.claims.cuboid.Cuboid;
import dev.hcr.hcf.factions.commands.member.FactionMapCommand;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.factions.types.SafeZoneFaction;
import dev.hcr.hcf.factions.types.WarzoneFaction;
import dev.hcr.hcf.factions.types.WildernessFaction;
import dev.hcr.hcf.utils.LocationUtils;
import dev.hcr.hcf.utils.backend.ConfigurationType;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Faction extends Claim {
    private final UUID uniqueID;
    private final String name;
    private ChatColor color = ChatColor.WHITE;
    private final Collection<Claim> claims = new ArrayList<>();
    private static WildernessFaction wilderness;
    private static WarzoneFaction warzone;
    private static final ConcurrentHashMap<UUID, Faction> factionUUIDMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Faction> factionNameMap = new ConcurrentHashMap<>();


    public Faction(UUID uuid, String name, Claim claim) {
        super(name, claim.getCuboid());
        if (claim.getCuboid() != null) {
            claims.add(new Claim(name, claim.getCuboid()));
        }
        this.uniqueID = uuid;
        this.name = name;
        factionUUIDMap.put(uniqueID, this);
        factionNameMap.put(name.toLowerCase(), this);

        if (this instanceof WildernessFaction) {
            wilderness = (WildernessFaction) this;
        }
        if (this instanceof WarzoneFaction) {
            warzone = (WarzoneFaction) this;
        }
    }

    public Faction(UUID uuid, String name) {
        super(name);
        this.uniqueID = uuid;
        this.name = name;
        factionUUIDMap.put(uniqueID, this);
        factionNameMap.put(name.toLowerCase(), this);
        if (this instanceof WildernessFaction) {
            wilderness = (WildernessFaction) this;
        }
        if (this instanceof WarzoneFaction) {
            warzone = (WarzoneFaction) this;
        }
    }

    /**
     * Loads faction from database implementation.
     * @param document - Document from MongoDB database.
     */
    public Faction(Document document) {
        super(document.getString("name"));
        this.uniqueID = UUID.fromString(document.getString("uuid"));
        this.name = document.getString("name");
        factionUUIDMap.put(uniqueID, this);
        factionNameMap.put(name.toLowerCase(), this);
        if (this instanceof WildernessFaction) {
            wilderness = (WildernessFaction) this;
        }
        if (this instanceof WarzoneFaction) {
            warzone = (WarzoneFaction) this;
        }
    }

    public Document save() {
        Document document = new Document("uuid", getUniqueID().toString());
        document.append("name", getName());
        document.append("color", (getColor() == null ? ChatColor.WHITE.toString() : getColor().name()));
        if (hasClaims()) {
            List<String> c = new ArrayList<>();
            for (Claim claim : getClaims()) {
                System.out.println("Found claim: " + claim.getName());
                c.add(claim.getCuboid().getPoint1().getX() + "*" + claim.getCuboid().getPoint1().getZ() + "*" + claim.getCuboid().getPoint2().getX() + "*" + claim.getCuboid().getPoint2().getZ() + "*" + claim.getCuboid().getPoint1().getWorld().getName());                System.out.println("Added claim!");
            }
            document.append("claims", c);
        }
        return document;
    }

    public void load(Document document) {
        if (document.containsKey("claims")) {
            List<String> claims = document.get("claims", ArrayList.class);
            for (String s : claims) {
                this.claims.add(new Claim(name, LocationUtils.parseCuboid(s)));
            }
        }
        if (document.containsKey("color")) {
            this.color = ChatColor.valueOf(document.getString("color"));
        }
    }

    public void disband() {
        // Start termination process of this faction.
        // First remove from db.
        HCF.getPlugin().getMongoImplementation().findFactionAndDelete(uniqueID);
        // Now lets remove the faction from all mappings
        factionNameMap.remove(name);
        factionUUIDMap.remove(uniqueID);
    }

    public UUID getUniqueID() {
        return uniqueID;
    }

    public String getName() {
        return name;
    }

    public boolean hasClaims() {
        return !claims.isEmpty();
    }

    public boolean hasClaim(Claim claim) {
        return claims.contains(claim);
    }

    public boolean addClaim(Cuboid cuboid) {
        // Check if the claim is appliceable
        //  1. The claim is not overlapping other territory
        //  2. The claim is not in WarZone (if playerfacion)
        //  3. The faction is not raidble or on regen


        // If its a player faction claiming, lets remove all other claims
        if (this instanceof PlayerFaction) {
            claims.clear();
        }
        claims.add(new Claim(getName(), cuboid));
        return true;
    }

    public double getClaimingLandPrice(Location location, Location location2) {
        // Huge fucking brain fart while trying to do simple math, here is an extremely cracked methed and just pure heroine way of doing something very simple
        double distance = location.distance(location2);
        double total = 0D;
        for (int block = 0; block < distance; block++) {
            total += block * 3;
        }
        return total;
    }

    public ChatColor getColor() {
        return color;
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    public String getDisplayName() {
        if (this.color != null) {
            return color + name;
        } else {
            return name;
        }
    }

    public Collection<Claim> getClaims() {
        return claims;
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

    public static SafeZoneFaction getSafeZone() {
        return (SafeZoneFaction) factionUUIDMap.values().stream().filter(faction -> faction instanceof SafeZoneFaction).findAny().orElse(null);
    }

    public static ArrayList<Faction> getNearByFactions(Location location) {
        ArrayList<Faction> near = new ArrayList<>();
        for (int x = -25; x <= 25; x++) {
            for (int z = -25; z <= 25; z++) {
                if (x == 0 || z == 0) continue;
                Faction faction = Faction.getByLocation(new Location(location.getWorld(), location.getBlockX() + x, location.getBlockY(), location.getBlockZ() + z));
                if (faction == null) continue;
                if (near.contains(faction)) continue;
                near.add(faction);
            }
        }
        return near;
    }

    public static WildernessFaction getWilderness() {
        return wilderness;
    }

    public static WarzoneFaction getWarzone() {
        return warzone;
    }

    public static Faction getByLocation(Location location) {
        for (Faction faction : getFactions()) {
            if (!faction.hasClaims()) continue;
            for (Claim claim : faction.getClaims()) {
                if (claim.getCuboid().isIn(location)) {
                    return faction;
                }
            }
        }
        int radius = ConfigurationType.getConfiguration("faction.properties").getInteger("warzone-radius");
        if (Math.abs(location.getBlockX()) <= radius && Math.abs(location.getBlockZ()) <= radius) {
            return getWarzone();
        }
        return getWilderness();
    }
}
