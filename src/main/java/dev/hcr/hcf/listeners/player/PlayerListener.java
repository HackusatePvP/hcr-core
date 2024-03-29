package dev.hcr.hcf.listeners.player;

import dev.hcr.hcf.pvpclass.PvPClass;
import dev.hcr.hcf.pvpclass.events.ClassEquippedEvent;
import dev.hcr.hcf.pvpclass.events.ClassUnequippedEvent;
import dev.hcr.hcf.scoreboard.events.PlayerInvisibilityEvent;
import dev.hcr.hcf.scoreboard.events.PlayerVisibilityEvent;
import dev.hcr.hcf.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerListener implements Listener {
    private final List<UUID> invisList = new ArrayList<>();

    @EventHandler
    public void onClassEquip(ClassEquippedEvent event) {
        Player player = event.getPlayer();
        PvPClass pvPClass = event.getPvPClass();
        player.sendMessage(CC.translate("&7Activating &c" + pvPClass.getDisplayName().toUpperCase() + " &7class."));
        pvPClass.equip(player);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onClassUnequip(ClassUnequippedEvent event) {
        Player player = event.getPlayer();
        PvPClass pvPClass = event.getPvPClass();
        player.sendMessage(CC.translate("&7Deactivating &c" + pvPClass.getDisplayName().toUpperCase() + " &7class."));
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        // Since bukkit doesnt have any potion events we ride with the move event
        Player player = event.getPlayer();
        if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            if (!invisList.contains(player.getUniqueId())) {
                System.out.println("Setting player to invis teams");
                PlayerInvisibilityEvent event1 = new PlayerInvisibilityEvent(player);
                Bukkit.getPluginManager().callEvent(event1);
                invisList.add(player.getUniqueId());
            }
        } else {
            if (invisList.contains(player.getUniqueId())) {
                System.out.println("Removing player from invis teams");
                PlayerVisibilityEvent event1 = new PlayerVisibilityEvent(player);
                Bukkit.getPluginManager().callEvent(event1);
                invisList.remove(player.getUniqueId());
            }
        }
    }
}
