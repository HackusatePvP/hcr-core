package dev.hcr.hcf.listeners.player;

import dev.hcr.hcf.pvpclass.events.ClassEquippedEvent;
import dev.hcr.hcf.pvpclass.events.ClassUnequippedEvent;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.TaskUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PvPClassListener implements Listener {

    @EventHandler
    public void onClassEquipEvent(ClassEquippedEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        event.getPvPClass().equip(player);
    }

    @EventHandler
    public void onClassUnequipEvent(ClassUnequippedEvent event) {
        Player player = event.getPlayer();
        TaskUtils.runSync(() -> {
            event.getPvPClass().unequip(player);
        });
    }
}
