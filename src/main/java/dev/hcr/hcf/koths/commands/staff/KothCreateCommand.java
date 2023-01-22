package dev.hcr.hcf.koths.commands.staff;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.koths.KothFaction;
import dev.hcr.hcf.koths.commands.KothCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class KothCreateCommand extends KothCommand {

    public KothCreateCommand() {
        super("create", "Create a new koth faction.", "create");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (args.length == 2) {
            if (Faction.getFactionByName(args[1]) != null) {
                sender.sendMessage(ChatColor.RED + "Faction already exists.");
                return;
            }
            new KothFaction(args[1]);
            sender.sendMessage(ChatColor.GREEN + "Created new faction.");
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return new ArrayList<>();
    }
}
