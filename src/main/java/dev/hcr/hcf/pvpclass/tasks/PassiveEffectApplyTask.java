package dev.hcr.hcf.pvpclass.tasks;

import dev.hcr.hcf.pvpclass.PvPClass;
import dev.hcr.hcf.pvpclass.types.bard.BardClass;
import dev.hcr.hcf.users.User;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public class PassiveEffectApplyTask extends BukkitRunnable {
    private final Player player;
    private final PvPClass pvPClass;

    public PassiveEffectApplyTask(Player player, PvPClass pvPClass) {
        this.player = player;
        this.pvPClass = pvPClass;
    }


    @Override
    public void run() {
        User user = User.getUser(player.getUniqueId());
        if (user.getCurrentClass() == null) return;
        if (user.getCurrentClass().getEffects(player) != null)
            Arrays.stream(user.getCurrentClass().getEffects(player)).forEach(effect -> {
                if (player.hasPotionEffect(effect.getType())) {
                    PotionEffect effect1 = player.getActivePotionEffects().stream().filter(effect2 -> effect2.getType() == effect.getType()).findAny().orElse(null);
                    if (effect1 == null) return;
                    if (effect1.getAmplifier() >= effect.getAmplifier()) {
                        player.addPotionEffect(effect1, true);
                    }
                } else {
                    player.addPotionEffect(effect.getEffect());
                }
            });

        if (user.getCurrentClass() instanceof BardClass) {
            if (player.getItemInHand() != null && !(player.getItemInHand().getType().equals(Material.AIR))) {
                ((BardClass) user.getCurrentClass()).applyHeldBard(player, player.getItemInHand());
            }
        }
    }
}