package dev.hcr.hcf.pvpclass.tasks;

import dev.hcr.hcf.pvpclass.PvPClass;
import dev.hcr.hcf.pvpclass.tasks.objects.Effect;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class PassiveEffectApplyTask extends BukkitRunnable {

    @Override
    public void run() {
        for (UUID uuid : PvPClass.getTrackerEntries()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            PvPClass pvPClass = PvPClass.getEquippedClass(player);
            for (Effect effect : pvPClass.getEffects(player)) {
                // Scan to see if the player has the potion effect in use, this could be a clickable effect so we don't want to replace the effect.
                PotionEffect foundEffect = player.getActivePotionEffects().stream().filter(effect1 -> effect1.getType().getName().equalsIgnoreCase(effect.getType().getName())).findAny().orElse(null);
                if (foundEffect == null) {
                    PvPClass.getPlayerEffectTask(player).addEffect(effect);
                }
            }
        }
    }
}
