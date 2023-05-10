package dev.hcr.hcf.factions.commands.member;

import dev.hcr.hcf.factions.commands.FactionCommand;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.users.faction.ChatChannel;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FactionChatCommand extends FactionCommand {

    public FactionChatCommand() {
        super("chat", "Change your current chat channel.", "", new String[]{"c"});
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

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        String[] channels = new String[] {"toggled", "public", "faction", "captain", "ally"};
        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], Arrays.asList(channels), completions);
        }
        Collections.sort(completions);
        return completions;
    }
}
