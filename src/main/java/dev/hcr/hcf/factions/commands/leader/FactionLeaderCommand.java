package dev.hcr.hcf.factions.commands.leader;

import dev.hcr.hcf.factions.commands.FactionCommand;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.users.faction.Role;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FactionLeaderCommand extends FactionCommand {
    public FactionLeaderCommand() {
        super("leader", "Transfers ownership of the faction to another member.");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        User user = User.getUser(player.getUniqueId());
        if (!user.hasFaction()) {
            player.sendMessage(ChatColor.RED + "You cannot transfer ownership of a faction that doesn't exist. /f create");
            return;
        }
        PlayerFaction playerFaction = (PlayerFaction) user.getFaction();
        if (playerFaction.getRole(user) != Role.LEADER) {
            player.sendMessage(ChatColor.RED + "Only leaders can transfer ownership.");
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
            User usr = User.getUser(targetUUID);
            PlayerFaction targetFaction = (PlayerFaction) usr.getFaction();
            if (!targetFaction.getName().equalsIgnoreCase(playerFaction.getName())) {
                player.sendMessage(ChatColor.RED + "You can only transfer ownership to members in your own faction.");
                return;
            }

        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
