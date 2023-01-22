package dev.hcr.hcf.commands.players.lives;

import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LivesCheckCommand extends LivesCommand {

    public LivesCheckCommand() {
        super("check", "Check how many lives you currently have.");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        User user = User.getUser(player.getUniqueId());
        player.sendMessage(CC.translate("&7Lives: &c" + user.getLives()));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
