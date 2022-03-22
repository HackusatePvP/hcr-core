package dev.hcr.hcf.listeners.factions;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.structure.Relation;
import dev.hcr.hcf.factions.types.SystemFaction;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.factions.types.WarzoneFaction;
import dev.hcr.hcf.factions.types.WildernessFaction;
import dev.hcr.hcf.hooks.PluginHook;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.users.faction.ChatChannel;
import dev.hcr.hcf.utils.CC;
import dev.hcr.hcf.utils.backend.ConfigurationType;
import jdk.nashorn.internal.ir.Block;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class FactionListener implements Listener {

    @EventHandler
    public void onTeamDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player && event.getDamager() instanceof Player)) return;
        Player pAttacker = (Player) event.getDamager();
        Player player = (Player) event.getEntity();
        User user = User.getUser(player.getUniqueId());
        User attacker = User.getUser(pAttacker.getUniqueId());
        if (user.getFaction().getUniqueID() == attacker.getFaction().getUniqueID()) {
            boolean teamDamage = ConfigurationType.getConfiguration("faction.properties").getBoolean("team-damage");
            if (!teamDamage) {
                event.setCancelled(true);
                pAttacker.sendMessage(CC.translate("&7You cannot hurt &c" + player.getName()));
            }
        }
    }

    @EventHandler
    public void onFactionMemeberDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        User user = User.getUser(player.getUniqueId());
        if (user.getFaction() == null) return;
        PlayerFaction faction = (PlayerFaction) user.getFaction();
        // TODO: 1/30/2022 Get the current location of the players death, see if this location is a faction and check to see if it has dtr multipliers
        faction.decreaseDTR(ConfigurationType.getConfiguration("faction.properties").getDouble("dtr-loss-per-death"));
    }

    @EventHandler
    public void onMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        Location from = event.getFrom();
        if (to.getBlockX() != from.getBlockX() || to.getBlockZ() != from.getBlockZ()) {
            // TODO: 1/30/2022 create custom events for entering and leaving territory
            Faction factionTo = Faction.getByLocation(to);
            Faction factionFrom = Faction.getByLocation(from);
            if (factionTo != null && factionTo != factionFrom) {
                if (factionFrom instanceof SystemFaction) {
                    player.sendMessage(CC.translate("&7Leaving: " + factionFrom.getColor() + factionFrom.getName()));
                } else {
                    player.sendMessage(CC.translate("&7Leaving: " + Relation.getFactionRelationship(factionFrom, player).getColor() + factionFrom.getName()));
                }
                if (factionTo instanceof SystemFaction) {
                    player.sendMessage(CC.translate("&7Entering: " + factionTo.getColor() + factionTo.getName()));
                } else {
                    player.sendMessage(CC.translate("&7Entering: " + Relation.getFactionRelationship(factionTo, player).getColor() + factionTo.getName()));
                }
            }
        }
    }
}
