package dev.hcr.hcf.pvpclass.tasks;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnergyBuildTask extends BukkitRunnable {
    private final Player player;
    private int energy;

    private final static Map<UUID, Integer> energyTracker = new HashMap<>();

    public EnergyBuildTask(Player player) {
        this.player = player;
    }


    @Override
    public void run() {
        energyTracker.put(player.getUniqueId(), energy++);
    }

    public static Integer getEnergy(Player player) {
        return energyTracker.get(player.getUniqueId());
    }
}
