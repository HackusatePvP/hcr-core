package dev.hcr.hcf.factions;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.factions.claims.Claim;
import dev.hcr.hcf.factions.claims.cuboid.Cuboid;
import dev.hcr.hcf.factions.types.*;
import dev.hcr.hcf.factions.types.roads.*;
import dev.hcr.hcf.utils.LocationUtils;
import dev.hcr.hcf.utils.backend.types.PropertiesConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Faction extends Claim {
    private final UUID uniqueID;
    private final String name;
    private ChatColor color = ChatColor.WHITE;
    private final Collection<Claim> claims = new ArrayList<>();
    private Location home;
    private final boolean deathban;
    private static WildernessFaction wilderness;
    private static WarzoneFaction warzone;
    private static RoadFaction northRoad, southRoad, eastRoad, westRoad;
    private static GlowStoneMountainFaction glowStoneMountainFaction;
    private static final ConcurrentHashMap<UUID, Faction> factionUUIDMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Faction> factionNameMap = new ConcurrentHashMap<>();

    private final boolean debug = PropertiesConfiguration.getPropertiesConfiguration("hcf.properties").getBoolean("debug");

    public Faction(UUID uuid, String name, Claim claim, boolean deathban) {
        super(name, claim.getCuboid());
        if (claim.getCuboid() != null) {
            claims.add(new Claim(name, claim.getCuboid()));
        }
        this.uniqueID = uuid;
        this.name = name;
        this.deathban = deathban;
        factionUUIDMap.put(uniqueID, this);
        factionNameMap.put(name.toLowerCase(), this);
        if (this instanceof WildernessFaction) {
            wilderness = (WildernessFaction) this;
        }
        if (this instanceof WarzoneFaction) {
            warzone = (WarzoneFaction) this;
        }
        if (this instanceof GlowStoneMountainFaction) {
            glowStoneMountainFaction = (GlowStoneMountainFaction) this;
        }
    }

    public Faction(UUID uuid, String name, boolean deathban) {
        super(name);
        this.uniqueID = uuid;
        this.name = name;
        this.deathban = deathban;
        factionUUIDMap.put(uniqueID, this);
        factionNameMap.put(name.toLowerCase(), this);
        if (this instanceof WildernessFaction) {
            wilderness = (WildernessFaction) this;
        }
        if (this instanceof WarzoneFaction) {
            warzone = (WarzoneFaction) this;
        }
        if (this instanceof GlowStoneMountainFaction) {
            glowStoneMountainFaction = (GlowStoneMountainFaction) this;
        }
    }

    public Faction(Map<String, Object> map) {
        super((String) map.get("name"));
        this.uniqueID = UUID.fromString((String) map.get("uuid"));
        this.name = (String) map.get("name");
        this.deathban = (Boolean) map.get("deathban");
        factionUUIDMap.put(uniqueID, this);
        factionNameMap.put(name.toLowerCase(), this);
        if (this instanceof WildernessFaction) {
            wilderness = (WildernessFaction) this;
        }
        if (this instanceof WarzoneFaction) {
            warzone = (WarzoneFaction) this;
        }
        if (this instanceof GlowStoneMountainFaction) {
            glowStoneMountainFaction = (GlowStoneMountainFaction) this;
        }
        if (this instanceof NorthRoad) {
            northRoad = (NorthRoad) this;
        }
        if (this instanceof EastRoad) {
            eastRoad = (EastRoad) this;
        }
        if (this instanceof SouthRoad) {
            southRoad = (SouthRoad) this;
        }
        if (this instanceof WestRoad) {
            westRoad = (WestRoad) this;
        }
    }

    public void save() {
        Map<String, Object> map = new HashMap<>();
        map.put("uuid", uniqueID.toString());
        map.put("name", getName());
        map.put("deathban", deathban);
        map.put("color",  (getColor() == null ? ChatColor.WHITE.toString() : getColor().name()));
        if (hasClaims()) {
            List<String> c = new ArrayList<>();
            for (Claim claim : getClaims()) {
                if (c.contains(claim.getCuboid().getPoint1().getX() + "*" + claim.getCuboid().getPoint1().getZ() + "*" + claim.getCuboid().getPoint2().getX() + "*" + claim.getCuboid().getPoint2().getZ() + "*" + claim.getCuboid().getPoint1().getWorld().getName())) {
                    continue;
                }
                c.add(claim.getCuboid().getPoint1().getX() + "*" + claim.getCuboid().getPoint1().getZ() + "*" + claim.getCuboid().getPoint2().getX() + "*" + claim.getCuboid().getPoint2().getZ() + "*" + claim.getCuboid().getPoint1().getWorld().getName());
            }
            map.put("claims", c);
        }
        if (home != null) {
            String parse = home.getX() + "%" + home.getY() + "%" + home.getZ() + "%" + home.getWorld().getName();
            map.put("home", parse);
        }
        HCF.getPlugin().getStorage().appendFactionData(map);
    }

    public void load(Map<String, Object> map) {
        if (map.containsKey("claims")) {
            List<String> claims = (ArrayList<String>) map.get("claims");
            if (claims == null || claims.isEmpty()) {
                if (debug) {
                    System.out.println("Claims is null or empty skipping: " + name);
                }
                return;
            }
            for (String s : claims) {
                //System.out.println("Found Claim: " + s);
                this.claims.add(new Claim(name, LocationUtils.parseCuboid(s)));
            }
        }
        if (map.containsKey("color")) {
            this.color = ChatColor.valueOf((String) map.get("color"));
        }
        if (map.containsKey("home")) {
            this.home = LocationUtils.parseLocation((String) map.get("home"));
        }
    }

    public void disband() {
        HCF.getPlugin().getStorage().findFactionAndDelete(uniqueID);
        factionNameMap.remove(name.toLowerCase());
        factionUUIDMap.remove(uniqueID);
    }

    public UUID getUniqueID() {
        return uniqueID;
    }

    public String getName() {
        return name.replace("_", " ");
    }

    public boolean isDeathBan() {
        return deathban;
    }

    public boolean hasClaims() {
        return !claims.isEmpty();
    }

    public boolean hasClaim(Claim claim) {
        return claims.contains(claim);
    }

    public boolean inClaim(Location location) {
        for (Claim claim : claims) {
            if (claim.getCuboid().isIn(location)) {
                return true;
            }
        }
        return false;
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
        save();
        return true;
    }

    public void unclaim() {
        this.claims.clear();
        this.home = null;
        save();
    }

    public void clearClaims() {
        claims.clear();
    }

    public double getClaimingLandPrice(Location location, Location location2) {
        PropertiesConfiguration configuration = PropertiesConfiguration.getPropertiesConfiguration("claims.properties");
        double distance = location.distance(location2);
        double total = 0D;
        int point1x = location.getBlockX();
        int point1z = location.getBlockZ();
        int point2x = location2.getBlockX();
        int point2z = location2.getBlockZ();

        int xCalc = Math.abs(point1x - point2x);
        int zCalc = Math.abs(point1z - point2z);
        total = (xCalc + zCalc) * configuration.getDouble("unclaim-price-per-block");
        return total;
    }

    public double getLandRefundPrice() {
        PropertiesConfiguration configuration = PropertiesConfiguration.getPropertiesConfiguration("claims.properties");
        double total = 0D;
        Claim claim = claims.stream().findFirst().orElse(null);
        if (claim != null) {
            int point1x = claim.getCuboid().getPoint1().getBlockX();
            int point1z = claim.getCuboid().getPoint1().getBlockZ();
            int point2x = claim.getCuboid().getPoint2().getBlockX();
            int point2z = claim.getCuboid().getPoint2().getBlockZ();

            int xCalc = Math.abs(point1x - point2x);
            int zCalc = Math.abs(point1z - point2z);
            total = (xCalc + zCalc) * configuration.getDouble("unclaim-price-per-block");

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

    public Location getHome() {
        return home;
    }

    public void setHome(Location home) {
        this.home = home;
    }

    public abstract double getDTRMultiplier();

    /* All static methods below */

    public static Collection<Player> getNearbyPlayers(Player player, int range) {
        Collection<Player> toReturn = new HashSet<>();
        for (Entity entity : player.getNearbyEntities(range, range, range)) {
            if (!(entity instanceof Player)) continue;
            toReturn.add((Player) entity);
        }
        return toReturn;
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

    public static ArrayList<Faction> getNearByFactions(Location location, int buffer) {
        ArrayList<Faction> near = new ArrayList<>();
        for (int x = -buffer; x <= buffer; x++) {
            for (int z = -buffer; z <= buffer; z++) {
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

    public static GlowStoneMountainFaction getGlowStoneMountainFaction() {
        return glowStoneMountainFaction;
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
        int radius = PropertiesConfiguration.getPropertiesConfiguration("faction.properties").getInteger("warzone-radius");
        if (Math.abs(location.getBlockX()) <= radius && Math.abs(location.getBlockZ()) <= radius) {
            return getWarzone();
        }
        return getWilderness();
    }

    public static RoadFaction getRoadFaction(String road) {
        switch (road.toLowerCase()) {
            case "north":
                return northRoad;
            case "east":
                return eastRoad;
            case "south":
                return southRoad;
            case "west":
                return westRoad;

        }
        return null;
    }
}
