package dev.hcr.hcf.factions;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.factions.claims.Claim;
import dev.hcr.hcf.factions.claims.cuboid.Cuboid;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.factions.types.SafeZoneFaction;
import dev.hcr.hcf.factions.types.WarzoneFaction;
import dev.hcr.hcf.factions.types.WildernessFaction;
import dev.hcr.hcf.utils.LocationUtils;
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
        System.out.println("Attempting to create document!");
        Document document = new Document("uuid", getUniqueID().toString());
        System.out.println("Created faction document!");
        document.append("name", getName());
        System.out.println("Appended name!");
        if (hasClaims()) {
            List<String> c = new ArrayList<>();
            System.out.println("Claims detected!");
            for (Claim claim : getClaims()) {
                System.out.println("Found claim: " + claim.getName());
                c.add(claim.getCuboid().getPoint1().getX() + "*" + claim.getCuboid().getPoint1().getZ() + "*" + claim.getCuboid().getPoint2().getX() + "*" + claim.getCuboid().getPoint2().getZ() + "*" + claim.getCuboid().getPoint1().getWorld().getName());                System.out.println("Added claim!");
            }
            document.append("claims", c);
            System.out.println("Appended all claims!");
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

    public void setFactionColor(ChatColor color) {
        this.color = color;
    }

    public ChatColor getFactionColor() {
        return color;
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

    public static WildernessFaction getWilderness() {
        return wilderness;
    }

    public static WarzoneFaction getWarzone() {
        return warzone;
    }

    public static Faction getByLocation(Location location) {
        // First check to see if the player is in WarZone.
        Location spawn = new Location(Bukkit.getWorld("world"), 0, location.getY(), 0);
        double distance = spawn.distance(location);
        if (distance < 500) {
            // TODO: 1/31/2022 Make the radius of warzone configurable, for now its just set to 500
            return warzone;
        }
        for (Faction faction : factionUUIDMap.values()) {
            for (Claim claim : faction.getClaims()) {
                if (claim.getCuboid() == null) {
                    continue;
                }
                if (claim.getCuboid().isIn(location)) {
                    return faction;
                }
            }
        }
        return wilderness; // Return warzone or Wilderness.
    }

    public UUID getUniqueID() {
        return uniqueID;
    }

    public String getName() {
        return name;
    }

}
