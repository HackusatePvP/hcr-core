package dev.hcr.hcf.listeners.entities;

import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ZombieListener implements Listener {

    @EventHandler
    public void onZombieAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Zombie && event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            // Hardcore additions: When a player is attacked by a zombie they will gain poison hunger and weakness.
            // HCR is deigned to have mobs enabled on most servers they are disabled. Having mobs enabled adds more of a
            // survival mechanic and adds a little bit of difficulty for early game.
            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 10, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20 * 30, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 30, 0));
        }
    }
}
