package dev.hcr.hcf.listeners.player;

import dev.hcr.hcf.pvpclass.events.ClassEquippedEvent;
import dev.hcr.hcf.pvpclass.events.ClassUnequippedEvent;
import dev.hcr.hcf.timers.types.player.ClassWarmupTimer;
import dev.hcr.hcf.users.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PvPClassListener implements Listener {

    @EventHandler
    public void onClassEquipEvent(ClassEquippedEvent event) {
        Player player = event.getPlayer();
        new ClassWarmupTimer(player);
    }

    @EventHandler
    public void onClassUnequipEvent(ClassUnequippedEvent event) {
        Player player = event.getPlayer();
        User user = User.getUser(player.getUniqueId());
        user.getCurrentClass().unequip(player);
    }
}
