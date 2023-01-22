package dev.hcr.hcf.pvpclass.types.bard.objects;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Effect {
    private final PotionEffect effect;
    private final int amplifier;
    private final boolean passive, selfApply, held, debuff;

    public Effect(PotionEffect effect, boolean passive) {
        this.effect = effect;
        this.amplifier = effect.getAmplifier();
        this.passive = passive;
        this.selfApply = false;
        this.debuff = false;
        this.held = false;
    }

    public Effect(PotionEffect effect, boolean passive, boolean selfApply) {
        this.effect = effect;
        this.amplifier = effect.getAmplifier();
        this.passive = passive;
        this.selfApply = selfApply;
        this.debuff = false;
        this.held = false;
    }

    public Effect(PotionEffect effect, boolean passive, boolean selfApply, boolean held) {
        this.effect = effect;
        this.amplifier = effect.getAmplifier();
        this.passive = passive;
        this.selfApply = selfApply;
        this.held = held;
        this.debuff = false;
    }

    public Effect(PotionEffect effect, boolean passive, boolean selfApply, boolean held, boolean debuff) {
        this.effect = effect;
        this.amplifier = effect.getAmplifier();
        this.passive = passive;
        this.selfApply = selfApply;
        this.held = held;
        this.debuff = debuff;
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

    public boolean isHeld() {
        return held;
    }

    public boolean isDebuff() {
        return debuff;
    }
}
