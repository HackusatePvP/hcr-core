package dev.hcr.hcf.factions.commands.staff;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.commands.FactionCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class FactionTestCommand extends FactionCommand {

    public FactionTestCommand() {
        super("test", "test");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("Success!");
    }
}
