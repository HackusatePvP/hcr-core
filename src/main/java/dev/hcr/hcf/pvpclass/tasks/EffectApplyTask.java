package dev.hcr.hcf.pvpclass.tasks;

import dev.hcr.hcf.pvpclass.tasks.objects.Effect;
import dev.hcr.hcf.utils.TaskUtils;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EffectApplyTask extends BukkitRunnable {
    private final Player player;
    private final List<Effect> effects = new ArrayList<>();

    public EffectApplyTask(Player player, Effect... effects) {
        this.player = player;
        if (effects != null) {
            this.effects.addAll(Arrays.asList(effects));
        }
    }

    @Override
    public void run() {
        if (effects.isEmpty()) return;
        for (Effect effect : effects) {
            if (effect.isPassive()) {
                if (!player.hasPotionEffect(effect.getType())) {
                    addEffectToPlayer(effect);
                }
            } else {
                if (player.hasPotionEffect(effect.getType())) {
                   PotionEffect potionEffect = player.getActivePotionEffects().stream().filter(effect1 -> effect1.getType().getName().equalsIgnoreCase(effect.getType().getName())).findAny().orElse(null);
                   if (potionEffect == null) {
                       System.out.println("What???");
                       return;
                   }
                   if (potionEffect.getAmplifier() == effect.getAmplifier()) {
                       // If the amplifiers are the same we are looping this method to call an effect. If the amplifiers are the same we can skip adding the effect.
                       removeEffect(effect);
                       return;
                   }
                }
                addEffectToPlayer(effect);
            }
        }
    }

    private void addEffectToPlayer(Effect effect) {
        System.out.println("Adding effect synchronously");
        System.out.println("Passive Effect: " + effect.isPassive());
        TaskUtils.runSync(() -> {
            System.out.println("Scanning for existing effects...");
            System.out.println("Effect: " + effect.getType());
            System.out.println(player.getActivePotionEffects().toString());
            PotionEffect foundEffect = player.getActivePotionEffects().stream().filter(effect1 -> effect1.getType().getName().equals(effect.getType().getName())).findAny().orElse(null);
            if (foundEffect != null) {
                System.out.println("existing effects found...");
                System.out.println("Existing effect: " + foundEffect.toString());
                if (effect.getAmplifier() > foundEffect.getAmplifier()) {
                    System.out.println("replacing effects...");
                    player.removePotionEffect(effect.getType());
                    player.addPotionEffect(effect.getEffect());
                }
            } else {
                System.out.println("Effects not found adding...");
                player.addPotionEffect(effect.getEffect());
            }
        });
    }

    public void addEffect(Effect effect) {
        Effect foundEffect = effects.stream().filter(effect1 -> effect1.getType().getName().equals(effect.getType().getName())).findAny().orElse(null);
        if (foundEffect != null) {
            effects.remove(foundEffect);
        }
        this.effects.add(effect);
    }

    public void removeEffect(Effect effect) {
        this.effects.remove(effect);
    }

    public List<Effect> getEffects() {
        return effects;
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        for (Effect effect : effects) {
            TaskUtils.runSync(() -> {
                player.removePotionEffect(effect.getType());
            });
        }
        super.cancel();
    }
}
