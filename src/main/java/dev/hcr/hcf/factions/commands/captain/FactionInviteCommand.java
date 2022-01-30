package dev.hcr.hcf.factions.commands.captain;

import dev.hcr.hcf.factions.commands.FactionCommand;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.CC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionInviteCommand extends FactionCommand {

    public FactionInviteCommand() {
        super("invite");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return;
        }
        if (!args[0].equalsIgnoreCase("invite")) return;
        Player player = (Player) sender;
        User inviter = User.getUser(player.getUniqueId());
        PlayerFaction inviterFaction = (PlayerFaction) inviter.getFaction();
        if (inviterFaction == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to invite a player!");
            return;
        }
        if (inviterFaction.getRole(inviter).getWeight() <= 1) {
            player.sendMessage(ChatColor.RED + "Only captains and above can invite players!");
            return;
        }
        if (args.length == 2) {
            String name = args[1];
            User user = User.getUser(name);
            if (user == null) {
                sender.sendMessage("&cCould not find user \"" + name + "\".");
                return;
            }
            player.sendMessage(CC.translate((inviterFaction.sendInvite(player, user) ? "&aYou have successfully invited &b" + user.getName() + "&a." : "&cCould not invite &b" + user.getName() + "&c.")));
        }
    }
}
