package dev.hcr.hcf.commands.players;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        User user = User.getUser(player.getUniqueId());
        player.sendMessage(CC.translate("&7Balance: &c" + HCF.getPlugin().getFormat().format(user.getBalance())));
        return false;
    }
}
