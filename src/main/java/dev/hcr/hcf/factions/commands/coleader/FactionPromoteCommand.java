package dev.hcr.hcf.factions.commands.coleader;

import dev.hcr.hcf.factions.commands.FactionCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class FactionPromoteCommand extends FactionCommand {

    public FactionPromoteCommand() {
        super("promote", "promote", "Promote a member in the faction to captain.");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {

    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
