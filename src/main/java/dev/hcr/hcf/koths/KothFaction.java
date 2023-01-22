package dev.hcr.hcf.koths;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.claims.cuboid.Cuboid;
import dev.hcr.hcf.factions.types.SystemFaction;
import dev.hcr.hcf.utils.LocationUtils;
import org.bson.Document;
import org.bukkit.Location;

import java.util.UUID;

public class KothFaction extends Faction implements SystemFaction {
    private Location center;
    private int radius;
    private Cuboid capZone;
    private boolean active;

    private final HCF plugin = HCF.getPlugin();

    public KothFaction(String name) {
        super(UUID.randomUUID(), name, true);
    }

    public KothFaction(Document document) {
        super(document);
        load(document);
    }

    @Override
    public void load(Document document) {
        if (document.containsKey("center")) {
            this.center = LocationUtils.parseLocation(document.getString("center"));
        }
        if (document.containsKey("radius")) {
            this.radius = document.getInteger("radius");
        }
        super.load(document);
    }

    @Override
    public double getDTRMultiplier() {
        return 2;
    }

    @Override
    public void save() {
        Document document = new Document("uuid", getUniqueID().toString());
        if (center != null) {
            document.append("center", LocationUtils.parseLocationToString(center));
        }
        if (radius > 0) {
            document.append("radius", radius);
        }
        plugin.getStorage().appendFactionData(document);
        super.save();
    }

    public void start(boolean forced) {
        if (forced) {

        } else {

        }
    }

    public Location getCenter() {
        return center;
    }

    public int getRadius() {
        return radius;
    }

    public void setCenter(Location center) {
        this.center = center;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public Cuboid getCapZone() {
        return capZone;
    }

    public void setCapZone(Cuboid capZone) {
        this.capZone = capZone;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
