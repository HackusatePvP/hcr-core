package dev.hcr.hcf.listeners.factions;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.events.members.FactionTerritoryEnterEvent;
import dev.hcr.hcf.factions.events.members.FactionTerritoryLeaveEvent;
import dev.hcr.hcf.factions.events.members.FactionTerritoryMoveEvent;
import dev.hcr.hcf.factions.structure.Relation;
import dev.hcr.hcf.factions.types.*;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.CC;
import dev.hcr.hcf.utils.backend.ConfigurationType;
import dev.hcr.hcf.utils.backend.types.PropertiesConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class FactionListener implements Listener {

    @EventHandler
    public void onTeamDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player && event.getDamager() instanceof Player)) return;
        Player pAttacker = (Player) event.getDamager();
        Player player = (Player) event.getEntity();
        User user = User.getUser(player.getUniqueId());
        User attacker = User.getUser(pAttacker.getUniqueId());
        if (!user.hasFaction() || attacker.hasFaction()) return;
        if (user.getFaction().getUniqueID() == attacker.getFaction().getUniqueID()) {
            boolean teamDamage = PropertiesConfiguration.getPropertiesConfiguration("faction.properties").getBoolean("team-damage");
            if (!teamDamage) {
                event.setCancelled(true);
                pAttacker.sendMessage(CC.translate("&7You cannot hurt &c" + player.getName()));
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Faction faction = Faction.getByLocation(player.getLocation());
            if (faction instanceof SafeZoneFaction) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onFactionMemeberDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        User user = User.getUser(player.getUniqueId());
        if (user.getFaction() == null) return;
        PlayerFaction faction = (PlayerFaction) user.getFaction();
        double baseDTRLoss = PropertiesConfiguration.getPropertiesConfiguration("faction.properties").getDouble("dtr-loss-per-death");
        Faction locationFaction = Faction.getByLocation(player.getLocation());
        baseDTRLoss = baseDTRLoss * locationFaction.getDTRMultiplier();
        faction.decreaseDTR(baseDTRLoss);
    }

    @EventHandler
    public void onMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        Location from = event.getFrom();
        if (to.getBlockX() != from.getBlockX() || to.getBlockZ() != from.getBlockZ()) {
            Faction factionTo = Faction.getByLocation(to);
            Faction factionFrom = Faction.getByLocation(from);
            if (factionTo != null && factionTo != factionFrom) {
                FactionTerritoryEnterEvent enterEvent = new FactionTerritoryEnterEvent(factionTo, player);
                FactionTerritoryLeaveEvent leaveEvent = new FactionTerritoryLeaveEvent(factionFrom, player);
                Bukkit.getPluginManager().callEvent(enterEvent);
                Bukkit.getPluginManager().callEvent(leaveEvent);
                if (factionFrom instanceof SystemFaction) {
                    player.sendMessage(CC.translate("&7Leaving: " + factionFrom.getColor() + factionFrom.getDisplayName()));
                } else {
                    player.sendMessage(CC.translate("&7Leaving: " + Relation.getFactionRelationship(factionFrom, player).getColor() + factionFrom.getName()));
                }
                if (factionTo instanceof SystemFaction) {
                    player.sendMessage(CC.translate("&7Entering: " + factionTo.getColor() + factionTo.getDisplayName()));
                } else {
                    player.sendMessage(CC.translate("&7Entering: " + Relation.getFactionRelationship(factionTo, player).getColor() + factionTo.getName()));
                }
            }
        }
    }

    @EventHandler
    public void onFactionTerritoryMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        Location from = event.getFrom();
        if (to.getBlockX() != from.getBlockX() || to.getBlockZ() != from.getBlockZ()) {
            Faction fromFaction = Faction.getByLocation(to);
            Faction toFaction = Faction.getByLocation(from);
            if (fromFaction == toFaction && (!(fromFaction instanceof WildernessFaction)) && (!(fromFaction instanceof WarzoneFaction))) {
                FactionTerritoryMoveEvent moveEvent = new FactionTerritoryMoveEvent(toFaction, player);
                Bukkit.getPluginManager().callEvent(moveEvent);
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        Faction faction = Faction.getByLocation(player.getLocation());
        if (faction instanceof SafeZoneFaction) {
            event.setCancelled(true);
        }
    }
}
