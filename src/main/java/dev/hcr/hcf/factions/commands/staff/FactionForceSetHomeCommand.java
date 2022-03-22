package dev.hcr.hcf.factions.commands.staff;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.commands.FactionCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class FactionForceSetHomeCommand extends FactionCommand {

    public FactionForceSetHomeCommand() {
        super("forcesethome", "forcesethome", "Force set a factions home");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        if (args.length == 2) {
            Faction faction = Faction.getFactionByName(args[1]);
            if (faction == null) {
                player.sendMessage(ChatColor.RED + "Could not find faction by the name of \"" + args[1] + "\".");
                return;
            }
            faction.setHome(player.getLocation());
            player.sendMessage(ChatColor.GREEN + "Successfully updated the home of " + faction.getName() + ".");
            // TODO: 2/22/2022 broadcast new home to faction faction.broadcast();
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " forcesethome <faction>");
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
