package dev.hcr.hcf.factions.commands.staff;

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

public class FactionForceLeaderCommand extends FactionCommand {

    public FactionForceLeaderCommand() {
        super("forceleader", "forceleader", "Forcefully set yourself or a player as a leader in a faction.");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        User user;
        if (args.length == 1) {
            user = User.getUser(player.getUniqueId());
            if (!user.hasFaction()) {
                player.sendMessage(ChatColor.RED + "You must be in a faction to force leader yourself.");
                return;
            }
            PlayerFaction playerFaction = (PlayerFaction) user.getFaction();
            if (playerFaction.getLeader() == user.getUniqueID()) {
                player.sendMessage(ChatColor.RED + "You are already the leader of this faction.");
                return;
            }
            playerFaction.setLeader(playerFaction.getLeader(), user.getUniqueID());
        }
        if (args.length == 2) {
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
            user = User.getUser(targetUUID);
            if (!user.hasFaction()) {
                player.sendMessage(ChatColor.RED + "\"" + user.getName() + "\" is not in a faction.");
                return;
            }
            PlayerFaction playerFaction = (PlayerFaction) user.getFaction();
            if (playerFaction.getLeader() == user.getUniqueID()) {
                player.sendMessage(ChatColor.RED + "\"" + user.getName() + "\" is already the leader of this faction.");
                return;
            }
            playerFaction.setLeader(playerFaction.getLeader(), user.getUniqueID());
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> players = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> players.add(player.getName()));
        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], players, completions);
        }
        Collections.sort(completions);
        return completions;
    }
}
