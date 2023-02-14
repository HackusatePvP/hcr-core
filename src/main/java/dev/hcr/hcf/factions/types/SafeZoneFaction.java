package dev.hcr.hcf.factions.types;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.claims.Claim;
import dev.hcr.hcf.factions.claims.cuboid.Cuboid;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.Map;
import java.util.UUID;

public class SafeZoneFaction extends Faction implements SystemFaction {

    public SafeZoneFaction() {
        super(UUID.randomUUID(), "SafeZone", new Claim("SafeZone", new Cuboid(new Location(Bukkit.getWorld("world"), 50, 0, 50), new Location(Bukkit.getWorld("world"), -50, 250, -50))), false);
        this.setColor(ChatColor.GREEN);
    }

    public SafeZoneFaction(Map<String, Object> map) {
        super(map);
        super.load(map);
    }

    @Override
    public double getDTRMultiplier() {
        return 1;
    }
}
