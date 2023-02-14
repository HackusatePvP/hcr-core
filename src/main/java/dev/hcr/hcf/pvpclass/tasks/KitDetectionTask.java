package dev.hcr.hcf.pvpclass.tasks;

import dev.hcr.hcf.pvpclass.PvPClass;
import dev.hcr.hcf.pvpclass.events.ClassEquippedEvent;
import dev.hcr.hcf.pvpclass.events.ClassUnequippedEvent;
import dev.hcr.hcf.timers.types.player.ClassWarmupTimer;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.TaskUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class KitDetectionTask extends BukkitRunnable {

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            User user = User.getUser(player.getUniqueId());
            if (!player.isOnline()) {
                continue;
            }
            PvPClass pvPClass = PvPClass.getEquippedClass(player);
            if (pvPClass != null) {
                // If they have a class equipped lets check to make sure its still applicable.
                if (PvPClass.getApplicableClass(player) == null ) {
                    // If the class is not applicable unequip the class.
                    ClassUnequippedEvent event = new ClassUnequippedEvent(pvPClass, player);
                    Bukkit.getPluginManager().callEvent(event);
                    user.setCurrentClass(null);
                }
            } else {
                // If they do not have a class equip check to see if a class is appliceable
                PvPClass foundClass = PvPClass.getApplicableClass(player);
                if (foundClass != null && !user.hasActiveTimer("class_warmup") && user.getCurrentClass() == null) {
                    // A class was found, equip the class and update the user class var
                    new ClassWarmupTimer(player);
                }
            }
        }
    }
}
