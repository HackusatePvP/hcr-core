package dev.hcr.hcf.factions.commands.captain;

import dev.hcr.hcf.factions.commands.FactionCommand;
import dev.hcr.hcf.users.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FactionInvestmentCommand extends FactionCommand {

    public FactionInvestmentCommand() {
        super("investment", "Manage your factions investment plan.");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        User user = User.getUser(player.getUniqueId());
        if (args.length < 2) {
            List<String> message = new ArrayList<>();
            message.add("&7&m----------------------------------------------------------");
            message.add("&4Faction Investment");
            message.add("&7You can invest money into your faction. The money invested is slowly increased overtime.");
            message.add("");
            message.add("&c/" + label + " invest <deposit|withdraw> <amount>");
            message.add("&7&m----------------------------------------------------------");
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
