package dev.hcr.hcf.factions.commands.staff;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.commands.FactionCommand;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.factions.types.SystemFaction;
import dev.hcr.hcf.users.User;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class FactionForceJoinCommand extends FactionCommand {

    public FactionForceJoinCommand() {
        super("forcejoin", "forcejoin", "Forcefully join another faction.");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        User user = User.getUser(player.getUniqueId());
        if (args.length == 2) {
            Faction faction = Faction.getFactionByName(args[1]);
            if (faction == null) {
                player.sendMessage(ChatColor.RED + "Could not find faction by the name of \"" + args[1] + "\".");
                return;
            }
            if (!(faction instanceof PlayerFaction)) {
                player.sendMessage(ChatColor.RED + "You cannot join system factions. To modify system factions use; /f claimfor, /f forcesethome, /f setcolor");
                return;
            }
            PlayerFaction playerFaction = (PlayerFaction) faction;
            if (user.getFaction() != null) {
                player.sendMessage(ChatColor.RED + "Leaving current faction to forcefully join &n" + playerFaction.getName());
                user.setFaction(null);
            }
            playerFaction.addUserToFaction(user);
            player.sendMessage(ChatColor.GREEN + "Successfully join " + playerFaction.getHome());
        } else {
            player.sendMessage(ChatColor.RED + "Usage: /" + label);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
