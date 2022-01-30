package dev.hcr.hcf.listeners;

import dev.hcr.hcf.factions.structure.Relation;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.users.faction.ChatChannel;
import dev.hcr.hcf.utils.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
                        recipient.sendMessage(CC.translate("&7[" + Relation.getFactionRelationship(playerFaction, recipient) + "&7] " + player.getName() + ": ") + event.getMessage());
                    }
                }
                break;
        }

    }
}
