package dev.hcr.hcf.koths.commands.player;

import dev.hcr.hcf.koths.commands.KothCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DefaultKothCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command c, String label, String[] args) {
        if (args.length == 0) {
            // Print out koth help command.
            KothCommand helpCommand = KothCommand.getCommand("help");
            if (helpCommand != null) {
                helpCommand.execute(sender, label, args);
            }
            return true;
        }
        KothCommand command = KothCommand.getCommand(args[0]);
        if (command == null) {
            sender.sendMessage(ChatColor.RED + "Could not find argument \"" + args[0] + "\". /" + label + " help");
            return true;
        }
        if (command.getPermission() != null && !command.getPermission().contains("hcf.faction.commands.null")) {
            if (!sender.hasPermission(command.getPermission())) {
                sender.sendMessage(ChatColor.RED + "No permission.");
                return true;
            }
        }
        command.execute(sender, label, args);
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commandArgs = Arrays.asList("create", "schedule", "setcenterzone");
        StringUtil.copyPartialMatches(args[0], commandArgs, completions);
        Collections.sort(completions);
        return completions;
    }
}
