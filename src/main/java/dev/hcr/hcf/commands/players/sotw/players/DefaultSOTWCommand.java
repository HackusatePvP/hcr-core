package dev.hcr.hcf.commands.players.sotw.players;

import dev.hcr.hcf.commands.players.sotw.SOTWCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultSOTWCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command c, String label, String[] args) {
        if (args.length == 0) {
            SOTWCommand helpCommand = SOTWCommand.getCommand("help");
            if (helpCommand != null) {
                helpCommand.execute(sender, c, label, args);
            }
            return true;
        }
        SOTWCommand command = SOTWCommand.getCommand(args[0]);
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
        command.execute(sender, c, label, args);
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command c, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], SOTWCommand.getRegisteredCommands(), completions);
        }
        if (args.length == 2) {
            SOTWCommand command = SOTWCommand.getCommand(args[0]);
            if (command != null) {
                StringUtil.copyPartialMatches(args[1], command.tabComplete(sender, c, alias, args), completions);
            }
        }
        Collections.sort(completions);
        return completions;
    }
}
