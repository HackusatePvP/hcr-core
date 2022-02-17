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
import org.bukkit.event.player.PlayerMoveEvent;

public class FactionListener implements Listener {
    private final PluginHook core;

    public FactionListener(HCF plugin) {
        this.core = plugin.getCore();
    }

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
                    String prefix = core.getPrefix(core.getRankByPlayer(player));
                    String suffix = core.getSuffix(core.getRankByPlayer(player));
                    ChatColor rankColor = core.getRankColor(core.getRankByPlayer(player));
                    String format = CC.translate((prefix.isEmpty() ? "" : prefix + " ") + rankColor + player.getName() + ChatColor.RESET + " " + (suffix.isEmpty() ? "" : suffix) + "&f");
                    if (playerFaction == null) {
                        recipient.sendMessage(format + ": " + ChatColor.WHITE + event.getMessage());
                    } else {
                        recipient.sendMessage(CC.translate("&7[" + Relation.getFactionRelationship(playerFaction, recipient).getColor() + playerFaction.getName() + "&7] " + format + ChatColor.WHITE + ": ") + event.getMessage());
                    }
                }
                break;
            case FACTION:
                if (playerFaction == null) {
                    player.sendMessage(ChatColor.RED + "You are not in a faction to use faction chat. Reverting to public to chat...");
                    user.setChannel(ChatChannel.PUBLIC);
                    break;
                }
                for (Player recipient : playerFaction.getOnlineMembers()) {
                    recipient.sendMessage(ChatColor.LIGHT_PURPLE + "[" + playerFaction.getName() + "] " + playerFaction.getRole(user).getAstrix() + " " + player.getName() + ": " + event.getMessage());
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

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();;
        event.setCancelled(preventTerritoryDamage(player, location));
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();
        event.setCancelled(preventTerritoryDamage(player, location));
    }

    private boolean preventTerritoryDamage(Entity entity, Location location) {
        if (!(entity instanceof Player)) {
            //  if the "entity" who is damaging the territory is not a player we will prevent it from doing damage
            return true;
        }
        Player player = (Player) entity;
        User user = User.getUser(player.getUniqueId());
        Faction factionAtLocation = Faction.getByLocation(location);
        if (factionAtLocation instanceof SystemFaction) {
            if (user.hasBypass()) return false;
            if (factionAtLocation instanceof WildernessFaction) {
                return false;
            } else if (factionAtLocation instanceof WarzoneFaction) {
                int buildRadius = ConfigurationType.getConfiguration("faction.properties").getInteger("warzone-build-radius");
                System.out.println(location.toString());
                System.out.println("Prevent build: " + (Math.abs(location.getBlockX()) <= buildRadius && Math.abs(location.getBlockZ()) <= buildRadius));
                return Math.abs(location.getBlockX()) <= buildRadius && Math.abs(location.getBlockZ()) <= buildRadius;
            } else {
                return true;
            }
        }
        if (factionAtLocation instanceof PlayerFaction) {
            PlayerFaction targetFaction = (PlayerFaction) factionAtLocation;
            if (user.getFaction() == null) return true;
            PlayerFaction playerFaction = (PlayerFaction) user.getFaction();
            return !playerFaction.getName().equals(targetFaction.getName());
        }
        return true;
    }
}
