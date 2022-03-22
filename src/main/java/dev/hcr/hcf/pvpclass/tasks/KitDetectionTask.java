package dev.hcr.hcf.pvpclass.tasks;

import dev.hcr.hcf.pvpclass.PvPClass;
import dev.hcr.hcf.pvpclass.events.ClassEquippedEvent;
import dev.hcr.hcf.pvpclass.events.ClassUnequippedEvent;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.TaskUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class KitDetectionTask extends BukkitRunnable {

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            System.out.println("Kit detection Running...");
            PvPClass pvPClass = PvPClass.getApplicableClass(player);
            User user = User.getUser(player.getUniqueId());
            System.out.println("Performing debug checks...");
            if (user.getCurrentClass() == null) {
                // If they do not currently have a class equipped we just have to active their new class.
                if (pvPClass == null) {
                    System.out.println("Applicable class is null, skipping...");
                    // dont do anything if we can't find the class.
                    continue;
                }
                if (user.getActiveTimer("class_warmup") != null) {
                    System.out.println("Player is already activating a class...");
                    continue;
                }
                System.out.println("All checks passed calling ClassEquippedEvent...");
                ClassEquippedEvent equipEvent = new ClassEquippedEvent(pvPClass, player);
                Bukkit.getPluginManager().callEvent(equipEvent);
                if (equipEvent.isCancelled()) {
                    continue;
                }
             /*   TaskUtils.runSync(() -> {
                    pvPClass.equip(player);
                });
                user.setCurrentClass(pvPClass); */
            } else {
                System.out.println("User currently has a class equipped. Checking to make sure its still equipped.");
                // If they do already have a class equip we must deactivate their class.
                if (pvPClass == null) {
                    System.out.println("User has unequipped the class passing events...");
                    ClassUnequippedEvent event = new ClassUnequippedEvent(user.getCurrentClass(), player);
                    Bukkit.getPluginManager().callEvent(event);
                    TaskUtils.runSync(() -> {
                        user.getCurrentClass().unequip(player);
                    });
                    user.setCurrentClass(null);
                }
            }
        }
    }
}
