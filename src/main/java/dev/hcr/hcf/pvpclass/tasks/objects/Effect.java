package dev.hcr.hcf.pvpclass.tasks.objects;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Effect {
    private final PotionEffect effect;
    private final int amplifier;
    private final boolean passive, selfApply;

    public Effect(PotionEffect effect, boolean passive, boolean selfApply) {
        this.effect = effect;
        this.amplifier = effect.getAmplifier();
        this.passive = passive;
        this.selfApply = selfApply;
    }

    public Effect(PotionEffect effect, boolean passive) {
        this.effect = effect;
        this.amplifier = effect.getAmplifier();
        this.passive = passive;
        this.selfApply = false;
    }

    public PotionEffect getEffect() {
        return effect;
    }

    public PotionEffectType getType() {
        return effect.getType();
    }

    public int getAmplifier() {
        return amplifier;
    }

    public boolean isPassive() {
        return passive;
    }

    public boolean isSelfApply() {
        return selfApply;
    }
}
