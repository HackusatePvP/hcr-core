package dev.hcr.hcf.listeners.factions;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.structure.Relation;
import dev.hcr.hcf.factions.types.SystemFaction;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.factions.types.WarzoneFaction;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.users.faction.ChatChannel;
import dev.hcr.hcf.utils.CC;
import dev.hcr.hcf.utils.backend.ConfigurationType;
import org.bukkit.Bukkit;
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
import org.bukkit.event.player.PlayerMoveEvent;

public class FactionListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        Player player = event.getPlayer();
        User user = User.getUser(player.getUniqueId());
        ChatChannel channel = user.getChannel();
        PlayerFaction playerFaction = (PlayerFaction) user.getFaction();

        switch (channel) {
            case TOGGLED:
                break;
            case PUBLIC:
                for (Player recipient : event.getRecipients()) {
                    User other = User.getUser(recipient.getUniqueId());
                    if (other.getChannel() == ChatChannel.TOGGLED) continue;
                    if (playerFaction == null) {
                        recipient.sendMessage(player.getName() + ": " + event.getMessage());
                    } else {
                        recipient.sendMessage(CC.translate("&7[" + Relation.getFactionRelationship(playerFaction, recipient).getColor() + playerFaction.getName() + "&7] " + player.getName() + ": ") + event.getMessage());
                    }
                }
                break;
        }
    }

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
        faction.decreaseDTR(ConfigurationType.getConfiguration("faction.properties").getDouble("dtr-multiplier"));
    }

    @EventHandler
    public void onMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        Location from = event.getFrom();
        if (to.getBlockX() != from.getBlockX() || to.getBlockZ() != from.getBlockZ()) {
            // TODO: 1/30/2022 create custom events for entering and leaving territory
            Faction factionTo = Faction.getByLocation(player.getLocation());
            Faction factionFrom = Faction.getByLocation(player.getLocation());
            if (factionTo != null && factionTo != factionFrom) {
                player.sendMessage(CC.translate("&cLeaving: " + Relation.getFactionRelationship(factionFrom, player).getColor() + factionFrom.getName()));
                player.sendMessage(CC.translate("&aEntering: " + Relation.getFactionRelationship(factionTo, player).getColor() + factionTo.getName()));
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();;
        //event.setCancelled(!canDamageTerritory(player, location));
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();
       // event.setCancelled(!canDamageTerritory(player, location));
    }

    private boolean canDamageTerritory(Entity entity, Location location) {
        if (!(entity instanceof Player)) {
            //  if the "entity" who is damaging the territory is not a player we will prevent it from doing damage
            return false;
        }
        Player player = (Player) entity;
        Faction factionLocation = Faction.getByLocation(location);
        if (factionLocation instanceof WarzoneFaction) {
            Location spawn = new Location(Bukkit.getWorld("world"), 0, location.getY(), 0);
            double distance = spawn.distance(location);
            if (distance > 300) {
                // TODO: 1/31/2022 make build radius configurable
                return true;
            }
        }
        if (factionLocation instanceof SystemFaction) {
            return false;
        }
        User user = User.getUser(player.getUniqueId());
        PlayerFaction playerFaction = (PlayerFaction) user.getFaction();

        if (factionLocation.getName().equalsIgnoreCase(playerFaction.getName())) {
            return true;
        }
        return false;
    }
}
