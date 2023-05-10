package dev.hcr.hcf.commands.players.sotw.players;

import dev.hcr.hcf.commands.players.sotw.SOTWCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class SOTWHelpCommand extends SOTWCommand {
    public SOTWHelpCommand() {
        super("help", "Displays the help page for the SOTW commands.");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        List<String> message = new ArrayList<>();
        message.add("&a&lSOTW &7Help Page (1/1)");
        message.add("&7&m--------------------------------------------------");
        if (sender.hasPermission("hcf.commands.sotw.admin")) {
            message.add("&c/" + label + " start <delay>");
            message.add("&c/" + label + " stop");
            //message.add("&c/" + label + " extend <delay>");
        } else {
            message.add("&c/" + label + " enable");
        }
        message.add("&7&m--------------------------------------------------");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
