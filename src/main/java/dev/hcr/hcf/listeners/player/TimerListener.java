package dev.hcr.hcf.listeners.player;

import dev.hcr.hcf.timers.Timer;
import dev.hcr.hcf.timers.types.player.EnderPearlTimer;
import dev.hcr.hcf.users.User;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;

public class TimerListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player player = (Player) event.getEntity();
            User user = User.getUser(player.getUniqueId());
            user.setTimer("combat", true);
            Player attacker = (Player) event.getDamager();
            if (attacker.getUniqueId() != player.getUniqueId()) {
                User target = User.getUser(attacker.getUniqueId());
                target.setTimer("combat", true);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            User user = User.getUser(player.getUniqueId());
            user.setTimer("combat", true);
        }
    }

    @EventHandler
    public void onEnderPearlThrow(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof EnderPearl && event.getEntity().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();
            User user = User.getUser(player.getUniqueId());
            if (!user.canEnderPearl()) {
                EnderPearlTimer enderPearlTimer = (EnderPearlTimer) user.getActiveTimer("enderpearl");
                player.sendMessage(ChatColor.RED + "You cannot throw another EnderPearl for " + enderPearlTimer.getDelay() + "");
                player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
                event.setCancelled(true);
            } else {
                user.setTimer("enderpearl", true);
            }
        }
    }
}
