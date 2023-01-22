package dev.hcr.hcf.listeners.player;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.factions.structure.Relation;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.hooks.PluginHook;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.users.faction.ChatChannel;
import dev.hcr.hcf.utils.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    private final PluginHook core;

    public ChatListener(HCF plugin) {
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
                    if (suffix == null || suffix.isEmpty()) {
                        suffix = "";
                    }
                    if (prefix == null || prefix.isEmpty()) {
                        prefix = "";
                    }
                    ChatColor rankColor = core.getRankColor(core.getRankByPlayer(player));
                    String format = CC.translate((prefix.isEmpty() ? "" : prefix + " ") + rankColor + player.getName() + ChatColor.RESET  + (suffix.isEmpty() ? "" : suffix) + "&f");
                    if (playerFaction == null) {
                        recipient.sendMessage(CC.translate("&7[&c-&7]") + " " + format + ": " + ChatColor.WHITE + event.getMessage());
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
                    recipient.sendMessage(CC.translate("&7[&5" + playerFaction.getName() + "&7] &d" + player.getName() + ": &7" + event.getMessage()));
                }
                break;
        }
    }
}
