package dev.hcr.hcf.koths;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.claims.cuboid.Cuboid;
import dev.hcr.hcf.factions.types.SystemFaction;
import dev.hcr.hcf.timers.Timer;
import dev.hcr.hcf.timers.types.server.KothTimer;
import dev.hcr.hcf.utils.LocationUtils;
import dev.hcr.hcf.utils.backend.types.PropertiesConfiguration;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KothFaction extends Faction implements SystemFaction {
    private Location center;
    private int radius;
    private Cuboid capZone;
    private boolean active;
    private Player capper;
    private KothTimer timer;

    private final boolean debug = PropertiesConfiguration.getPropertiesConfiguration("hcf.properties").getBoolean("debug");
    private final HCF plugin = HCF.getPlugin();

    public KothFaction(String name) {
        super(UUID.randomUUID(), name, true);
    }

    public KothFaction(Map<String, Object> map) {
        super(map);
        super.load(map);
        load(map);
    }

    @Override
    public void load(Map<String, Object> map) {
        if (map.containsKey("center")) {
            this.center = LocationUtils.parseLocation((String) map.get("center"));
        }
        if (map.containsKey("radius")) {
            this.radius = (Integer) map.get("radius");
        }
        Location corner1 = center.add(-radius, 0, -radius);
        Location corner2 = center.add(radius, 255, radius);
        setCapZone(new Cuboid(corner1, corner2));
        super.load(map);
    }

    @Override
    public String getDisplayName() {
        String[] display = getName().toLowerCase().split("koth");
        String name = display[0];
        name = name.substring(0, 1).toUpperCase() + name.replaceFirst(name.substring(0, 1), "");
        return name + " Koth";
    }

    @Override
    public double getDTRMultiplier() {
        return 2;
    }

    @Override
    public void save() {
        Map<String, Object> map = new HashMap<>();
        map.put("uuid", getUniqueID().toString());
        if (center != null) {
            map.put("center", LocationUtils.parseLocationToString(center));
        }
        if (radius > 0) {
            map.put("radius", radius);
        }
        plugin.getStorage().appendFactionData(map);
        super.save();
    }

    public void start(boolean forced) {
        if (forced) {
            if (debug) {
                System.out.println("Forcing koth...");
            }
            if (Timer.getTimer(getName()) != null) {
                if (debug) {
                    System.out.println("Timer already exists ending...");
                }
                Timer.getTimer(getName()).end(true);
            }
            if (debug) {
                System.out.println("Creating Timer...");
            }
            timer = new KothTimer(this);
            if (debug) {
                System.out.println("Running timer...");
            }
            timer.run();
            if (debug) {
                System.out.println("Creating Task...");
            }
            KothTask task = new KothTask(this, timer);
            if (debug) {
                System.out.println("Running task...");
            }
            task.runTaskTimer(HCF.getPlugin(), 0L, 20L);
            setActive(true);
        } else {
            if (Timer.getTimer(getName()) == null) {
                timer = new KothTimer(this);
                timer.run();
                KothTask task = new KothTask(this, timer);
                task.runTaskTimer(HCF.getPlugin(), 0L, 20L);
                setActive(true);
            }
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

    public Player getCapper() {
        return capper;
    }

    public void setCapper(Player capper) {
        this.capper = capper;
    }

    public KothTimer getTimer() {
        return timer;
    }
}
