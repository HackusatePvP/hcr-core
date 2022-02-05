package dev.hcr.hcf.factions.types;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.claims.Claim;
import dev.hcr.hcf.factions.claims.cuboid.Cuboid;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

public class SafeZoneFaction extends Faction implements SystemFaction {

    public SafeZoneFaction() {
        super(UUID.randomUUID(), "SafeZone", new Claim("SafeZone", new Cuboid(new Location(Bukkit.getWorld("world"), 50, 0, 50), new Location(Bukkit.getWorld("world"), -50, 250, -50))));
    }

    public SafeZoneFaction(Document document) {
        super(document);
        load(document);
    }

    @Override
    public void load(Document document) {

    }
}
