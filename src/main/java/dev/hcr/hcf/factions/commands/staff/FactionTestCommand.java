package dev.hcr.hcf.factions.commands.staff;

import dev.hcr.hcf.factions.commands.FactionCommand;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.users.User;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class FactionTestCommand extends FactionCommand {

    public FactionTestCommand() {
        super("test", "test", "test");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
       if ((!(sender instanceof Player))) return;
       Player player = (Player) sender;
       User user = User.getUser(player.getUniqueId());
       if (!user.hasFaction()) return;
       PlayerFaction playerFaction = (PlayerFaction) user.getFaction();
       player.sendMessage(ChatColor.RED + "Current DTR: " + playerFaction.getCurrentDTR());
       player.sendMessage(ChatColor.RED + "Max DTR: " + playerFaction.getMaxDTR());
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
