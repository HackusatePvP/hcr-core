package dev.hcr.hcf.factions.structure.regen;

import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.utils.backend.ConfigurationType;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class FactionRegenTask extends BukkitRunnable {
    private final Map<PlayerFaction, Long> factionPausedRegenCooldown = new HashMap<>();
    private final Map<PlayerFaction, Long> factionRegenTime = new HashMap<>();

    @Override
    public void run() {
        System.out.println("Task Running!");
        for (PlayerFaction faction : factionPausedRegenCooldown.keySet()) {
            long timeLeft = factionPausedRegenCooldown.get(faction) - System.currentTimeMillis();
            System.out.println(faction.getName() + ": " + DurationFormatUtils.formatDurationWords(timeLeft, true ,true));
            if (timeLeft <= 0L) {
                // set the faction to be no longer paused and setup reginning
                System.out.println(faction.getName() + " Completed Pause Cooldown");
                factionPausedRegenCooldown.remove(faction);
                factionRegenTime.put(faction, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30L));
                faction.setRegenStatus(RegenStatus.REGENERATING);
            }
        }
        for (PlayerFaction faction : factionRegenTime.keySet()) {
            System.out.println("Regen task running!");
            long timeLeft = factionRegenTime.get(faction);
            if (timeLeft <= 0L || faction.getCurrentDTR() >= faction.getMaxDTR()) {
                // If the dtr is greater than the max lets correct it
                if (faction.getCurrentDTR() > faction.getMaxDTR()) {
                    faction.setCurrentDTR(faction.getMaxDTR());
                }
                factionRegenTime.remove(faction);
                faction.setRegenStatus(RegenStatus.FULL);
                continue;
            }
            System.out.println("Increasing " + faction.getName() + " DTR by 0.0005");
            faction.increaseDTR(ConfigurationType.getConfiguration("faction.properties").getDouble("dtr-increment-per-second"));
            DecimalFormat format = new DecimalFormat("#.####");
            System.out.println("Current DTR: " + format.format(faction.getCurrentDTR()));
        }
    }

    public void setupFactionRegen(PlayerFaction playerFaction) {
        // TODO: 1/30/2022 Change 1 min to 30min
        factionPausedRegenCooldown.put(playerFaction, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10L));
        factionRegenTime.remove(playerFaction);
    }

    public void instantRegen(PlayerFaction playerFaction) {
        factionRegenTime.put(playerFaction, TimeUnit.MINUTES.toMillis(30));
    }
}
