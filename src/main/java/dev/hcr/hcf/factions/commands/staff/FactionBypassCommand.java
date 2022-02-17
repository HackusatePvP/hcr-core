package dev.hcr.hcf.factions.commands.staff;

import dev.hcr.hcf.factions.commands.FactionCommand;
import dev.hcr.hcf.users.User;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FactionBypassCommand extends FactionCommand {

    public FactionBypassCommand() {
        super("bypass", "bypass", "Enables you to interact with illegal territories such as SystemFactions and enemy territories.");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        User user = User.getUser(player.getUniqueId());
        user.setBypass(!user.hasBypass());
        player.sendMessage((user.hasBypass() ? ChatColor.GREEN + "Faction bypassing enabled." : ChatColor.RED + "Faction bypassing disabled."));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
