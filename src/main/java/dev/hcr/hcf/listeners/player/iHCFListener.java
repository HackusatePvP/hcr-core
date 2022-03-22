package dev.hcr.hcf.listeners.player;

import dev.hcr.hcf.pvpclass.PvPClass;
import dev.hcr.hcf.pvpclass.events.ClassEquippedEvent;
import dev.hcr.hcf.pvpclass.events.ClassUnequippedEvent;
import dev.hcr.hcf.pvpclass.tasks.EffectApplyTask;
import dev.hcr.hcf.pvpclass.tasks.objects.Effect;
import dev.hcr.hcf.users.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionEffectRemoveEvent;
import org.bukkit.event.inventory.EquipmentSetEvent;
import org.bukkit.potion.PotionEffect;

import java.util.Arrays;

/**
 * Most hcf cores have their own spigots which can handle specific events. Most refer to these events as iHCF events.
 * This includes PotionEffectEvents ArmorEquipEvents and more.
 *
 * DISCLAIMER: This core is not a fork of iHCF and does not use iHCF code. This listener just handles specific events that were designed for HCF.
 * The name iHCFListener just refers to those events, there is no correlation between this core and iHCF.
 */
public class iHCFListener implements Listener {

    @EventHandler
    public void onEquipmentSetEvent(EquipmentSetEvent event) {
        if (!(event.getHumanEntity() instanceof Player)) return;
        Player player = (Player) event.getHumanEntity();
        User user = User.getUser(player.getUniqueId());
        PvPClass applicableClass = PvPClass.getApplicableClass(player);
        if (user.getCurrentClass() == null) {
            if (applicableClass != null) {
                ClassEquippedEvent event1 = new ClassEquippedEvent(applicableClass, player);
                Bukkit.getPluginManager().callEvent(event1);
            }
        } else {
            if (applicableClass == null) {
                ClassUnequippedEvent event1 = new ClassUnequippedEvent(user.getCurrentClass(), player);
                Bukkit.getPluginManager().callEvent(event1);
            }
        }
    }

    @EventHandler
    public void onPotionEffectRemove(PotionEffectRemoveEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        EffectApplyTask task = PvPClass.getPlayerEffectTask(player);
        if (task == null) return;
        PotionEffect effect = event.getEffect();
        Effect foundEffect = task.getEffects().stream().filter(effect1 -> effect1.getType().getName().equalsIgnoreCase(effect.getType().getName())).findAny().orElse(null);
        if (foundEffect != null) {
            if (!foundEffect.isPassive()) {
                task.removeEffect(foundEffect);
                System.out.println("Scanning for passive effects...");
                PvPClass pvPClass = PvPClass.getEquippedClass(player);
                if (pvPClass == null) return;
                System.out.println("Player has class equipped, scanning for effects...");
                Effect passiveEffect = Arrays.stream(pvPClass.getEffects(player)).filter(effect1 -> effect1.getType().getName().equalsIgnoreCase(foundEffect.getType().getName())).findAny().orElse(null);
                if (passiveEffect != null) {
                    System.out.println("Effect found adding...");
                    task.addEffect(passiveEffect);
                }
            }
        }
    }
}
