package dev.hcr.hcf.pvpclass.tasks;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

public class EffectRestoreTask extends BukkitRunnable {
    private final Player player;
    private final PotionEffect before;

    public EffectRestoreTask(Player player, PotionEffect before) {
        this.player = player;
        this.before = before;
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            cancel();
            return;
        }

        if (player.hasPotionEffect(before.getType())) {
            PotionEffect effect = player.getActivePotionEffects().stream()
                    .filter(potionEffect -> potionEffect.getType() == before.getType())
                    .findFirst().orElse(null);
            if (effect == null)
                return;

            if (effect.getAmplifier() == before.getAmplifier()) {
                if (before.getDuration() > effect.getDuration())
                    player.addPotionEffect(before, true);
            } else {
                player.addPotionEffect(before, true);
            }
        } else {
            player.addPotionEffect(before);
        }
    }
}
