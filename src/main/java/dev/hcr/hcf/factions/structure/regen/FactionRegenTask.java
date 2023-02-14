package dev.hcr.hcf.factions.structure.regen;

import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.utils.backend.ConfigurationType;
import dev.hcr.hcf.utils.backend.types.PropertiesConfiguration;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class FactionRegenTask extends BukkitRunnable {
    private static final Map<PlayerFaction, Long> factionPausedRegenCooldown = new HashMap<>();
    private static final Map<PlayerFaction, Long> factionRegenTime = new HashMap<>();
    private static final PropertiesConfiguration configuration = PropertiesConfiguration.getPropertiesConfiguration("faction.properties");

    // Debug switch
    private final boolean debug = PropertiesConfiguration.getPropertiesConfiguration("hcf.properties").getBoolean("debug");

    @Override
    public void run() {
        for (PlayerFaction faction : factionPausedRegenCooldown.keySet()) {
            long timeLeft = factionPausedRegenCooldown.get(faction) - System.currentTimeMillis();
            if (debug) {
                System.out.println(faction.getName() + ": " + DurationFormatUtils.formatDurationWords(timeLeft, true, true));
            }
            if (timeLeft <= 0L) {
                // set the faction to be no longer paused and setup regen
                factionPausedRegenCooldown.remove(faction);
                factionRegenTime.put(faction, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30L));
                faction.setRegenStatus(RegenStatus.REGENERATING);
            }
        }
        for (PlayerFaction faction : factionRegenTime.keySet()) {
            if (debug) {
                System.out.println("Regen task running!");
            }
            long timeLeft = factionRegenTime.get(faction);
            if (timeLeft <= 0L || faction.getCurrentDTR() >= faction.getMaxDTR()) {
                // If the dtr is greater than the max lets correct it
                faction.setCurrentDTR(faction.getMaxDTR());
                factionRegenTime.remove(faction);
                faction.setRegenStatus(RegenStatus.FULL);
                continue;
            }
            factionRegenTime.put(faction, timeLeft);
            faction.increaseDTR(configuration.getDouble("regen-increment"));
        }
    }

    public void setupFactionRegen(PlayerFaction playerFaction) {
        factionPausedRegenCooldown.put(playerFaction, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(configuration.getInteger("regen-start-delay")));
        factionRegenTime.remove(playerFaction);
    }

    public void instantRegen(PlayerFaction playerFaction) {
        factionRegenTime.put(playerFaction, TimeUnit.MINUTES.toMillis(configuration.getInteger("regen-start-delay")));
    }

    public static boolean isWaitingRegeneration(PlayerFaction playerFaction) {
        return factionPausedRegenCooldown.containsKey(playerFaction);
    }

    public static Long getRegenPausedDelay(PlayerFaction playerFaction) {
        return factionPausedRegenCooldown.get(playerFaction);
    }

    public static Map<PlayerFaction, Long> getFactionPausedRegenCooldown() {
        return factionPausedRegenCooldown;
    }
}
