package dev.hcr.hcf.factions.structure;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.types.GlowStoneMountainFaction;
import dev.hcr.hcf.timers.Timer;
import dev.hcr.hcf.utils.CC;
import org.bukkit.Bukkit;

import java.util.TimerTask;

public class GlowStoneMountainTimerTask extends TimerTask {

    @Override
    public void run() {
        GlowStoneMountainFaction faction = Faction.getGlowStoneMountainFaction();
        if (faction == null) {
            cancel();
            return;
        }
        if (Timer.getTimer("SOTW") != null && Timer.getTimer("SOTW").isActive()) {
            faction.regenerateGlowStone();
            Bukkit.broadcast(CC.translate("&8[&6GlowstoneMouintain&8] &aAll glowstone has been regenerated."), "");
        }
    }
}
