package dev.hcr.hcf.factions.commands.member;

import dev.hcr.hcf.factions.commands.FactionCommand;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.users.faction.ChatChannel;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionChatCommand extends FactionCommand {

    public FactionChatCommand() {
        super("chat");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return;
        if (!args[0].equalsIgnoreCase("chat")) return;
        Player player = (Player) sender;
        User user = User.getUser(player.getUniqueId());
        if (args.length == 2) {
            String channel = args[1];
            user.setChannel(ChatChannel.getChannel(channel));
        }
    }
}
