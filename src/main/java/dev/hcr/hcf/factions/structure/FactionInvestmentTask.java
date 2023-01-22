package dev.hcr.hcf.factions.structure;

import dev.hcr.hcf.factions.types.PlayerFaction;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class FactionInvestmentTask extends BukkitRunnable {
    private final PlayerFaction playerFaction;

    private static final Set<PlayerFaction> factions = new HashSet<>();

    public FactionInvestmentTask(PlayerFaction playerFaction) {
        this.playerFaction = playerFaction;

        factions.add(playerFaction); // Keep track of factions who are gaining intrest.
    }

    @Override
    public void run() {
        if (playerFaction.getInvestment() <= 0) return;
        playerFaction.addInvestment(playerFaction.getInvestment() * 0.03);
    }

    public static boolean isGainingIntrest(PlayerFaction playerFaction) {
        return factions.contains(playerFaction);
    }
}
