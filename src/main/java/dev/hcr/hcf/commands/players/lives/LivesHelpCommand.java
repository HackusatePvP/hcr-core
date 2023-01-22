package dev.hcr.hcf.commands.players.lives;

import dev.hcr.hcf.utils.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class LivesHelpCommand extends LivesCommand {

    public LivesHelpCommand() {
        super("help", "Display command usages and information.");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        for (String s : LivesCommand.getRegisteredCommands()) {
            LivesCommand command = LivesCommand.getCommand(s);
            if (sender.hasPermission(command.getPermission())) {
                sender.sendMessage(CC.translate("&6* &c" + s + " &7" + command.getDescription()));
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
