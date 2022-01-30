package dev.hcr.hcf.factions.commands.staff;

import dev.hcr.hcf.factions.commands.FactionCommand;
import dev.hcr.hcf.utils.backend.ConfigurationType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class FactionTestCommand extends FactionCommand {

    public FactionTestCommand() {
        super("test", "test", "test");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        ConfigurationType configuration = ConfigurationType.getConfiguration("faction.properties");
        if (configuration == null) {
            sender.sendMessage("Could not find file!");
            return;
        }
        sender.sendMessage(configuration.getString("team-damage"));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
