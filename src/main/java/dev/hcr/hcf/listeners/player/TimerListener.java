package dev.hcr.hcf.listeners.player;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.events.members.FactionTerritoryEnterEvent;
import dev.hcr.hcf.factions.events.members.FactionTerritoryLeaveEvent;
import dev.hcr.hcf.factions.types.SafeZoneFaction;
import dev.hcr.hcf.timers.Timer;
import dev.hcr.hcf.timers.events.TimerStartEvent;
import dev.hcr.hcf.timers.types.player.EnderPearlTimer;
import dev.hcr.hcf.timers.types.player.PvPTimer;
import dev.hcr.hcf.users.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class TimerListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player player = (Player) event.getEntity();
            User user = User.getUser(player.getUniqueId());
            Player attacker = (Player) event.getDamager();
            User uAttack = User.getUser(attacker.getUniqueId());
            if (uAttack.hasActiveTimer("pvp")) {
                attacker.sendMessage(ChatColor.RED + "You cannot attack players whilst PvPTimer is active.");
                event.setCancelled(true);
                return;
            }
            if (Faction.getByLocation(attacker.getLocation()) instanceof SafeZoneFaction) {
                attacker.sendMessage(ChatColor.RED + "You cannot attack players whilst in a SafeZone faction.");
                event.setCancelled(true);
                return;
            }
            if (user.hasActiveTimer("pvp")) {
                attacker.sendMessage(ChatColor.RED + "You can attack " + user.getName() + " as they have an active PvPTimer.");
                event.setCancelled(true);
                return;
            }
            if (Faction.getByLocation(player.getLocation()) instanceof SafeZoneFaction) {
                attacker.sendMessage(ChatColor.RED + "You cannot attack players inside of a SafeZone faction.");
                event.setCancelled(true);
                return;
            }
            user.setTimer("combat", true);
            if (attacker.getUniqueId() != player.getUniqueId()) {
                User target = User.getUser(attacker.getUniqueId());
                target.setTimer("combat", true);
            }
        }
    }

    @EventHandler
    public void onTimerStart(TimerStartEvent event) {
        if (event.getTimer() instanceof PvPTimer) {
            PvPTimer timer = (PvPTimer) event.getTimer();
            Player player;
            if (timer.getPlayer() == null) {
                player = Bukkit.getPlayer(timer.getUuid());
            } else {
                player = timer.getPlayer();
            }
            Faction faction = Faction.getByLocation(player.getLocation());
            if (faction instanceof SafeZoneFaction) {
                timer.setPause(true);
            }
        }
    }

    @EventHandler
    public void onEnderPearlThrow(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof EnderPearl && event.getEntity().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();
            User user = User.getUser(player.getUniqueId());
            if (user.hasActiveTimer("enderpearl")) {
                EnderPearlTimer enderPearlTimer = (EnderPearlTimer) user.getActiveTimer("enderpearl");
                player.sendMessage(ChatColor.RED + "You cannot throw another EnderPearl for " + enderPearlTimer.getDelay() + "");
                player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
                event.setCancelled(true);
            } else {
                user.setTimer("enderpearl", true);
            }
        }
    }

    @EventHandler
    public void onFactionTerritoryEnter(FactionTerritoryEnterEvent event) {
        Player player = event.getPlayer();
        User user = User.getUser(player.getUniqueId());
        Faction faction = event.getFaction();
        if (user.hasActiveTimer("pvp")) {
            PvPTimer timer = (PvPTimer) user.getActiveTimer("pvp");
            if (timer == null) return;
            if (faction instanceof SafeZoneFaction) {
                timer.setPause(true);
                player.sendMessage(ChatColor.RED + "PvPTimer paused.");
            }
        }
    }

    @EventHandler
    public void onFactionTerritoryLeave(FactionTerritoryLeaveEvent event) {
        Player player = event.getPlayer();
        User user = User.getUser(player.getUniqueId());
        Faction faction = event.getFaction();
        if (user.hasActiveTimer("pvp")) {
            PvPTimer timer = (PvPTimer) user.getActiveTimer("pvp");
            if (timer == null) return;
            if (faction instanceof SafeZoneFaction) {
                timer.setPause(false);
                player.sendMessage(ChatColor.RED + "PvPTimer resumed.");
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = User.getUser(player.getUniqueId());
        Faction faction = Faction.getByLocation(player.getLocation());
        if (user.hasActiveTimer("pvp")) {
            PvPTimer timer = (PvPTimer) user.getActiveTimer("pvp");
            if (timer == null) return;
            if (faction instanceof SafeZoneFaction) {
                timer.setPause(true);
            }
        }
    }

}
