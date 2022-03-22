package dev.hcr.hcf.listeners.player;

import dev.hcr.hcf.pvpclass.PvPClass;
import dev.hcr.hcf.pvpclass.events.ClassEquippedEvent;
import dev.hcr.hcf.pvpclass.events.ClassUnequippedEvent;
import dev.hcr.hcf.utils.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerListener implements Listener {

    @EventHandler
    public void onClassEquip(ClassEquippedEvent event) {
        Player player = event.getPlayer();
        PvPClass pvPClass = event.getPvPClass();
        player.sendMessage(CC.translate("&7Activating &c" + pvPClass.getName().toUpperCase() + " &7class."));
    }

    @EventHandler
    public void onClassUnequip(ClassUnequippedEvent event) {
        Player player = event.getPlayer();
        PvPClass pvPClass = event.getPvPClass();
        player.sendMessage(CC.translate("&7Deactivating &c" + pvPClass.getName().toUpperCase() + " &7class."));
    }
}
