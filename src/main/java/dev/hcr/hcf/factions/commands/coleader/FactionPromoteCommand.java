package dev.hcr.hcf.factions.commands.coleader;

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

public class FactionPromoteCommand extends FactionCommand {

    public FactionPromoteCommand() {
        super("promote", "Promote a member in the faction to the next rank.");
    }


    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        User user = User.getUser(player.getUniqueId());
        if (!user.hasFaction()) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to promote players.");
            return;
        }
        PlayerFaction playerFaction = (PlayerFaction) user.getFaction();
        if (playerFaction.getRole(user).getWeight() < 3) {
            player.sendMessage(ChatColor.RED + "Only co-leaders and above can promote players.");
            return;
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
            User userTarget = User.getUser(targetUUID);

            if (!userTarget.hasFaction()) {
                player.sendMessage(ChatColor.RED + userTarget.getName() + " is mot in a faction.");
                return;
            }
            if (!playerFaction.hasMember(userTarget.getUniqueID())) {
                player.sendMessage(ChatColor.RED + userTarget.getName() + " is not in your faction.");
                return;
            }
            playerFaction.promoteUser(player, userTarget);
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
