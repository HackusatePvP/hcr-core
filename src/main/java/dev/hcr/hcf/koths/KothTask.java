package dev.hcr.hcf.koths;

import dev.hcr.hcf.timers.types.server.KothTimer;
import dev.hcr.hcf.utils.backend.types.PropertiesConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class KothTask extends BukkitRunnable {
    private final KothFaction faction;
    private final KothTimer timer;
    private Player previousCapper;

    private final boolean debug = PropertiesConfiguration.getPropertiesConfiguration("hcf.properties").getBoolean("debug");

    public KothTask(KothFaction faction, KothTimer timer) {
        this.faction = faction;
        this.timer = timer;
        timer.run();
    }

    @Override
    public void run() {
        if (debug) {
            System.out.println("Running koth...");
        }
        Player capper = faction.getCapper();
        if (capper == null) {
            if (debug) {
                System.out.println("Capper is null");
            }
            if (previousCapper != null) {
                System.out.println("Capper and previousCapper are null. Resetting timer...");
                timer.reset();
                previousCapper = null;
            }
            timer.reset();
            return;
        }
        if (previousCapper == null || capper.getUniqueId() != previousCapper.getUniqueId()) {
            if (debug) {
                System.out.println("Capper and previous capper are not the same resetting.");
            }
            timer.reset();
            timer.setPause(false);
            previousCapper = capper;
        }
        if (!faction.getCapZone().isIn(capper)) {
            if (debug) {
                System.out.println("Capper is no longer in the capzone resetting");
            }
            faction.setCapper(null);
            timer.reset();
            previousCapper = capper;
            return;
        }
        timer.setPause(false);
    }

}
