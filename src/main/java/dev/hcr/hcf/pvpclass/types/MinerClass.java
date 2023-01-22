package dev.hcr.hcf.pvpclass.types;

import dev.hcr.hcf.pvpclass.PvPClass;
import dev.hcr.hcf.pvpclass.events.ClassUnequippedEvent;
import dev.hcr.hcf.pvpclass.types.bard.objects.Effect;
import dev.hcr.hcf.users.User;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class MinerClass extends PvPClass implements Listener {

    public MinerClass() {
        super("miner");
    }

    @Override
    public String getDisplayName() {
        return "&7Miner";
    }

    @Override
    public Effect[] getEffects(Player player) {
        List<Effect> effects = new ArrayList<>();
        effects.add(new Effect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0), true));
        User user = User.getUser(player.getUniqueId());
        int diamonds = user.getUserStatistics().getOreStat("diamond");
        if (diamonds >= 25) {
            effects.add(new Effect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0), true));
        }
        if (diamonds >= 50) {
            effects.add(new Effect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0), true));
        }
        if (diamonds >= 75) {
            effects.add(new Effect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 3), true));
        } else {
            effects.add(new Effect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 2), true));
        }
        if (diamonds >= 100) {
            effects.add(new Effect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1), true));
        } else {
            effects.add(new Effect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0), true));
        }
        return effects.toArray(new Effect[0]);
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        User user = User.getUser(player.getUniqueId());
        if (user.getCurrentClass() != null) {
            for (Effect effect : user.getCurrentClass().getEffects(player)) {
                player.removePotionEffect(effect.getType());
            }
            ClassUnequippedEvent unequippedEvent = new ClassUnequippedEvent(this, player);
            Bukkit.getPluginManager().callEvent(unequippedEvent);
            user.setCurrentClass(null);
        }
    }

}
