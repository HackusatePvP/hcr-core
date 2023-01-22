package dev.hcr.hcf.pvpclass.tasks;

import dev.hcr.hcf.utils.backend.types.PropertiesConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class EnergyBuildTask extends BukkitRunnable {
    private final Player player;
    private int energy;


    public EnergyBuildTask(Player player) {
        this.player = player;
    }


    @Override
    public void run() {
        if (energy >= PropertiesConfiguration.getPropertiesConfiguration("faction.properties").getInteger("max-bard-energy")) {
            return;
        }
       energy++;
    }

    public Player getPlayer() {
        return player;
    }

    public int getEnergy() {
        return energy;
    }

    public void removeEnergy(int amount) {
        this.energy = energy - amount;
    }
}
