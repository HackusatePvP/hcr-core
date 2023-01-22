package dev.hcr.hcf.factions.commands.captain;

import dev.hcr.hcf.factions.commands.FactionCommand;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.users.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class FactionKickCommand extends FactionCommand {

    public FactionKickCommand() {
        super("kick", "Kick a member in your faction.");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return;
        if (args.length == 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + label + " kick <member>");
            return;
        }
        Player player = (Player) sender;
        User user = User.getUser(player.getUniqueId());
        if (user.getFaction() == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to kick a member.");
            return;
        }
        PlayerFaction playerFaction = (PlayerFaction) user.getFaction();
        Player target = Bukkit.getPlayer(args[1]);
        UUID targetUUID;
        if (target == null) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
            if (offlinePlayer == null) {
                player.sendMessage(ChatColor.RED + "Could not find player \"" + args[1] + "\".");
                return;
            }
            targetUUID = offlinePlayer.getUniqueId();
        } else {
            targetUUID = target.getUniqueId();
        }
        User usr = User.getUser(targetUUID);
        PlayerFaction targetFaction = (PlayerFaction) usr.getFaction();
        if (!targetFaction.getName().equalsIgnoreCase(playerFaction.getName())) {
            player.sendMessage(ChatColor.RED + "You can only kick members in your own faction.");
            return;
        }
        if (targetFaction.getRole(usr).getWeight() == 4) {
            player.sendMessage(ChatColor.RED + "You cannot kick the leader of the faction.");
            return;
        }
        if (playerFaction.getRole(user).getWeight() < 2) {
            player.sendMessage(ChatColor.RED + "You must be at least a captain in the faction to kick a member.");
            return;
        }
        if (playerFaction.getRole(user).getWeight() < targetFaction.getRole(usr).getWeight()) {
            player.sendMessage(ChatColor.RED + "You cannot kick members who have a higher status then you.");
            return;
        }
        if (!playerFaction.removeUserFromFaction(usr, false)) {
            player.sendMessage(ChatColor.RED + "Could not kick \"" + usr.getName() + "\" from faction.");
            return;
        }
        player.sendMessage(ChatColor.GREEN + "Successfully kicked \"" + usr.getName() + "\" from the faction.");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) return new ArrayList<>();
        List<String> completions = new ArrayList<>();
        Player player = (Player) sender;
        User user = User.getUser(player.getUniqueId());
        PlayerFaction faction = (PlayerFaction) user.getFaction();
        if (args.length == 2) {
            List<String> memberNames = new ArrayList<>();
            for (UUID uuid : faction.getFactionMembers()) {
                if (uuid == player.getUniqueId()) continue;
                User target = User.getUser(uuid);
                memberNames.add(target.getName());
            }
            StringUtil.copyPartialMatches(args[1], memberNames, completions);
        }
        Collections.sort(completions);
        return completions;
    }
}

