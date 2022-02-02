package dev.hcr.hcf.factions.types;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.claims.Claim;
import dev.hcr.hcf.factions.claims.cuboid.Cuboid;
import dev.hcr.hcf.factions.structure.SystemFaction;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
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

    @Override
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
}
