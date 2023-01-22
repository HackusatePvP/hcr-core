package dev.hcr.hcf.koths.commands.player;

import dev.hcr.hcf.koths.commands.KothCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
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
        if (!command.getPermission().replace("hcf.koth.commands.", "").equalsIgnoreCase("")) {
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
        return new ArrayList<>();
    }
}
