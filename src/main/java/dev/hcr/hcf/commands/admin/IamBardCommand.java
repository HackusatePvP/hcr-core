package dev.hcr.hcf.commands.admin;

import dev.hcr.hcf.pvpclass.tasks.EnergyBuildTask;
import dev.hcr.hcf.pvpclass.types.bard.BardClass;
import dev.hcr.hcf.users.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IamBardCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        User user = User.getUser(player.getUniqueId());
        if (user.getCurrentClass() != null && user.getCurrentClass() instanceof BardClass) {
            BardClass bardClass = (BardClass) user.getCurrentClass();
            EnergyBuildTask energyBuildTask = bardClass.getEnergyTracker(player);
            energyBuildTask.setEnergy(120);
        }
        return false;
    }
}
