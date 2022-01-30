package dev.hcr.hcf.listeners;

import dev.hcr.hcf.factions.structure.Relation;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.users.faction.ChatChannel;
import dev.hcr.hcf.utils.CC;
import dev.hcr.hcf.utils.backend.ConfigurationType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

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
}
