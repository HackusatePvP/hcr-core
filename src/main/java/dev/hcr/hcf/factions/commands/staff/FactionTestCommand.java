package dev.hcr.hcf.factions.commands.staff;

import dev.hcr.hcf.factions.commands.FactionCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class FactionTestCommand extends FactionCommand {

    public FactionTestCommand() {
        super("test", "test", "test");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("Success!");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
