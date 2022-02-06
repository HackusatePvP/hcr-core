package dev.hcr.hcf.factions.commands.member;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.commands.FactionCommand;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.CC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class FactionJoinCommand extends FactionCommand {

    public FactionJoinCommand() {
        super("join", "Accept an invite to a faction.");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        User user = User.getUser(player.getUniqueId());
        if (user.getFaction() != null) {
            player.sendMessage(ChatColor.RED + "You must leave your current faction before you can join a different one.");
            return;
        }
        if (args.length == 2) {
            String factionName = args[1];
            PlayerFaction faction = (PlayerFaction) Faction.getFactionByName(factionName);
            if (faction == null) {
                player.sendMessage(ChatColor.RED + "Could not find faction \"" + factionName + "\".");
                return;
            }
            if (!faction.hasInvite(player)) {
                player.sendMessage(ChatColor.RED + factionName + " has not invited you to their faction.");
                return;
            }
            if (!faction.addUserToFaction(user)) {
                player.sendMessage(ChatColor.RED + "You cannot join that faction at this time.");
                return;
            }
            faction.broadcast(CC.translate("&7[&4" + faction.getName().toUpperCase() + "&7] &c" + player.getName() + " &7has joined the faction!"));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
