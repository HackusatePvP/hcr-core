package dev.hcr.hcf.factions.commands.member;

import dev.hcr.hcf.factions.commands.FactionCommand;
import dev.hcr.hcf.utils.CC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.*;
public class FactionHelpCommand extends FactionCommand {
    private final TreeMap<String, Integer> pageMap = new TreeMap<>();

    public FactionHelpCommand() {
        super("help", "Display faction command information.", "", new String[]{"h"});
        pagination();
    }

    void pagination() {
        // Start pagination
        // add registered commands to new array
        List<String> commands = new ArrayList<>(FactionCommand.getRegisteredCommands());
        int items = commands.size();
        int count = 1;
        int page = 1;
        do {
            if (count == 10) {
                // For every 10 commands we will make a new page
                page++;
                count = 0;
            }
            String command = commands.get(items - 1);
            // Store command to the page map
            pageMap.put(command, page);
            // Remove the command from the array
            commands.remove(command);
            count++;
            --items;
        } while (!commands.isEmpty());
    }

    @Override
    public void execute(CommandSender sender, Command c, String label, String[] args) {
        List<String> message = new ArrayList<>();
        if (args.length < 2) {
            message.add("&7&m-------------------------------------------------------------");
            message.add("&cFaction Help &7(&c" + 1 + "&7/&c" + getMaxPage() + "&7)");
            message.add("");
            for (String s : pageMap.keySet()) {
                if (pageMap.get(s) == 1) {
                    FactionCommand command = FactionCommand.getCommand(s);
                    message.add("&6* &c" + command.getName() + ": &7" + command.getDescription());
                }
            }
            message.add("");
            message.add("&7You are on page (" + 1 + "/" + getMaxPage() + ") To view the next page use the command \"/" + label + " " + this.getName() + " <page>.\"");
            message.add("&7&m-------------------------------------------------------------");
        }
        if (args.length == 2) {
            message.add("&7&m-------------------------------------------------------------");
            int page = Integer.parseInt(args[1]);
            if (page > getMaxPage()) {
                message.add(ChatColor.RED + "This page doesn't exist!");
            } else {
                message.add("&cFaction Help &7(&c" + page + "&7/&c" + getMaxPage() + "&7)");
                message.add("");
                for (String s : pageMap.keySet()) {
                    if (pageMap.get(s) == page) {
                        FactionCommand command = FactionCommand.getCommand(s);
                        message.add("&6* &c" + command.getName() + ": &7" + command.getDescription());
                    }
                }
                message.add("");
                message.add("&7You are on page (" + 1 + "/" + getMaxPage() + ") To view the next page use the command \"/" + label + " " + this.getName() + " <page>.\"");
            }
            message.add("&7&m-------------------------------------------------------------");
        }
        message.forEach(msg -> sender.sendMessage(CC.translate(msg)));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> pages = new ArrayList<>();
        for (int page = 1; page < getMaxPage(); page++) {
            pages.add(page + "");
        }
        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], pages, completions);
        }
        Collections.sort(completions);
        return completions;
    }

    private int getMaxPage() {
        Map.Entry<String, Integer> maxEntry = Collections.max(pageMap.entrySet(), Map.Entry.comparingByValue());
        return maxEntry.getValue();
    }
}
