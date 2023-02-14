package dev.hcr.hcf.factions.commands.staff;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.commands.FactionCommand;
import dev.hcr.hcf.factions.types.SystemFaction;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FactionSetColorCommand extends FactionCommand {

    public FactionSetColorCommand() {
        super("setcolor", "color", "Sets a specific color to a faction.");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            sender.sendMessage(ChatColor.RED + "/" + label + " setcolor <faction> <ChatColor>");
        }
        if (args.length == 2) {
            sender.sendMessage(ChatColor.RED + "/" + label + " setcolor <" + args[1] + "> <ChatColor>");
        }
        if (args.length == 3) {
            Faction faction = Faction.getFactionByName(args[1]);
            if (faction == null) {
                sender.sendMessage(ChatColor.RED + "Could not find faction by the name of \"" + args[1] + "\".");
                return;
            }
            ChatColor chatColor = null;
            try {
                chatColor = ChatColor.valueOf(args[2]);
            } catch (IllegalArgumentException e) {
                sender.sendMessage(ChatColor.RED + "Invalid color: " + args[2]);
            } finally {
                if (chatColor != null) {
                    faction.setColor(chatColor);
                    sender.sendMessage(ChatColor.GREEN + "Successfully updated ChatColor for " + args[1] + ".");
                }
            }

        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 2) {
            List<String> systemFactions = new ArrayList<>();
            Faction.getFactions().forEach(faction -> {
                if (faction instanceof SystemFaction) {
                    systemFactions.add(faction.getName());
                }
            });
            StringUtil.copyPartialMatches(args[1], systemFactions, completions);
        } else if (args.length == 3) {
            List<String> colors = new ArrayList<>();
            Arrays.stream(ChatColor.values()).forEach(color -> colors.add(color.name()));
            StringUtil.copyPartialMatches(args[2], colors, completions);
        }
        Collections.sort(completions);
        return completions;
    }
}
