package dev.hcr.hcf.koths.commands.staff;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.koths.KothFaction;
import dev.hcr.hcf.koths.commands.KothCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class KothStartCommand extends KothCommand {
    public KothStartCommand() {
        super("start", "Starts a Koth", "start");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            sender.sendMessage(ChatColor.RED + "/" + label + " start <koth>");
        }
        if (args.length == 2) {
            Faction faction = Faction.getFactionByName(args[1]);
            if (faction == null) {
                sender.sendMessage(ChatColor.RED + "Could not find faction by the name of \"" + args[1] + "\".");
                return;
            }
            if (!(faction instanceof KothFaction)) {
                sender.sendMessage(ChatColor.RED + "The faction must be a koth faction.");
                return;
            }
            KothFaction kothFaction = (KothFaction) faction;
            kothFaction.start(true);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return null;
    }
}
