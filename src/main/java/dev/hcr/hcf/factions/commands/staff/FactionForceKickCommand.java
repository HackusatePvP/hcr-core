package dev.hcr.hcf.factions.commands.staff;

import dev.hcr.hcf.factions.commands.FactionCommand;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.PlayerUtils;
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

public class FactionForceKickCommand extends FactionCommand {

    public FactionForceKickCommand() {
        super("forcekick", "forcekick", "Forcefully kick someone from a faction.");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        if (args.length == 1) {
            player.sendMessage(ChatColor.RED + "Usage: /" + label + " forcekick <player>");
            return;
        }
        if (args.length == 2) {
            String name = args[1];
            User user = User.getUser(name);
            if (user == null) {
                UUID targetUUID;
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                if (offlinePlayer == null) {
                    player.sendMessage(ChatColor.RED + "Could not find player \"" + args[1] + "\".");
                    return;
                }
                targetUUID = offlinePlayer.getUniqueId();
                user = User.getUser(targetUUID, name);
                PlayerFaction faction = (PlayerFaction) user.getFaction();
                if (targetUUID == faction.getLeader()) {
                    player.sendMessage(ChatColor.RED + "You cannot kick the leader of a faction. Use /" + label + " forceleader \" and then /" + label + " forcekick \"");
                    return;
                }
                if (faction.removeUserFromFaction(user, true)) {
                    player.sendMessage(ChatColor.GREEN + "Kicked " + user.getName() + " from faction.");
                }
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> players = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> {
            User user = User.getUser(player.getUniqueId());
            if (user.hasFaction()) {
                players.add(player.getName());
            }
        });
        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], players, completions);
        }
        Collections.sort(completions);
        return completions;
    }
}
