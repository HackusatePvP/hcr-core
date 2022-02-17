package dev.hcr.hcf.pvpclass;

import dev.hcr.hcf.pvpclass.events.ClassEquippedEvent;
import dev.hcr.hcf.pvpclass.events.ClassUnequippedEvent;
import dev.hcr.hcf.users.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class KitDetectionTask extends BukkitRunnable {

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PvPClass pvPClass = PvPClass.getEquippedClass(player);
            User user = User.getUser(player.getUniqueId());
            if (user.getCurrentClass() == null) {
                // If they do not currently have a class equipped we just have to active their new class.
                if (pvPClass == null) {
                    // dont do anything if we can't find the class.
                    return;
                }
                ClassEquippedEvent equipEvent = new ClassEquippedEvent(pvPClass, player);
                Bukkit.getPluginManager().callEvent(equipEvent);
                if (equipEvent.isCancelled()) {
                    return;
                }
                pvPClass.equip(player);
                user.setCurrentClass(pvPClass);
            } else {
                // If they do already have a class equip we must deactivate their class.
                if (pvPClass == null) {
                    ClassUnequippedEvent event = new ClassUnequippedEvent(user.getCurrentClass(), player);
                    Bukkit.getPluginManager().callEvent(event);
                    user.getCurrentClass().unequip(player);
                    user.setCurrentClass(null);
                }
            }
        }
    }
}
