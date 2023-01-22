package dev.hcr.hcf.factions.commands.staff;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.commands.FactionCommand;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.factions.types.SafeZoneFaction;
import dev.hcr.hcf.utils.CC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
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
            if (faction instanceof PlayerFaction) {
                PlayerFaction playerFaction = (PlayerFaction) faction;
                playerFaction.broadcast(CC.translate("&7[&4" + getName().toUpperCase() + "&7] &c" + player.getName() + " &7has updated the faction home. &c[" + player.getLocation().getBlockX() + "," + player.getLocation().getBlockZ() + "]"));

            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " forcesethome <faction>");
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> completions = new ArrayList<>();
        ArrayList<String> factionNames = new ArrayList<>();
        Faction.getFactions().forEach(faction -> {
                factionNames.add(faction.getName());
        });
        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], factionNames, completions);
        }
        return completions;
    }
}
