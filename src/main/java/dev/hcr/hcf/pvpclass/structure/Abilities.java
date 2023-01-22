package dev.hcr.hcf.pvpclass.structure;

import dev.hcr.hcf.utils.backend.types.PropertiesConfiguration;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public enum Abilities {
    ARCHER_SPEED("archer_speed",0, PropertiesConfiguration.getPropertiesConfiguration("pvpclass.properties").getInteger("archer-speed-duration"), PropertiesConfiguration.getPropertiesConfiguration("pvpclass.properties").getInteger("archer-speed-amplifier"), TimeUnit.SECONDS.toMillis(60L)),
    ARCHER_RESISTANCE("archer_resistance", 0, PropertiesConfiguration.getPropertiesConfiguration("pvpclass.properties").getInteger("archer-resistance-duration"), PropertiesConfiguration.getPropertiesConfiguration("pvpclass.properties").getInteger("archer-resistance-amplifier"), TimeUnit.SECONDS.toMillis(60L)),
    BARD_SPEED("bard_speed",30, PropertiesConfiguration.getPropertiesConfiguration("pvpclass.properties").getInteger("bard-speed-duration"), PropertiesConfiguration.getPropertiesConfiguration("pvpclass.properties").getInteger("bard-speed-amplifier"), TimeUnit.SECONDS.toMillis(45L)),
    BARD_RESISTANCE("bard_resistance",40, PropertiesConfiguration.getPropertiesConfiguration("pvpclass.properties").getInteger("bard-resistance-duration"), PropertiesConfiguration.getPropertiesConfiguration("pvpclass.properties").getInteger("bard-resistance-amplifier"), TimeUnit.SECONDS.toMillis(60L)),
    BARD_STRENGTH("bard_strength",45, PropertiesConfiguration.getPropertiesConfiguration("pvpclass.properties").getInteger("bard-strength-duration"), PropertiesConfiguration.getPropertiesConfiguration("pvpclass.properties").getInteger("bard-strength-amplifier"), TimeUnit.SECONDS.toMillis(60L)),
    BARD_REGENERATION("bard_regeneration",35, PropertiesConfiguration.getPropertiesConfiguration("pvpclass.properties").getInteger("bard-regeneration-duration"), PropertiesConfiguration.getPropertiesConfiguration("pvpclass.properties").getInteger("bard-regeneration-amplifier"), TimeUnit.SECONDS.toMillis(60L)),
    BARD_JUMP_BOOST("bard_jump_boost",20, PropertiesConfiguration.getPropertiesConfiguration("pvpclass.properties").getInteger("bard-jump-boost-duration"), PropertiesConfiguration.getPropertiesConfiguration("pvpclass.properties").getInteger("bard-jump-boost-amplifier"), TimeUnit.SECONDS.toMillis(60L)),
    BARD_WITHER("bard_wither",40, PropertiesConfiguration.getPropertiesConfiguration("pvpclass.properties").getInteger("bard-wither-duration"), PropertiesConfiguration.getPropertiesConfiguration("pvpclass.properties").getInteger("bard-wither-amplifier"), TimeUnit.SECONDS.toMillis(60L)),
    ROGUE_SPEED("rogue_speed", 0, PropertiesConfiguration.getPropertiesConfiguration("pvpclass.properties").getInteger("rogue-speed-duration"), PropertiesConfiguration.getPropertiesConfiguration("pvpclass.properties").getInteger("rogue-speed-amplifier"), TimeUnit.SECONDS.toMillis(60L)),
    ROGUE_JUMP_BOOST("rogue_jump_boost", 0, PropertiesConfiguration.getPropertiesConfiguration("pvpclass.properties").getInteger("rogue-jump-boost-duration"), PropertiesConfiguration.getPropertiesConfiguration("pvpclass.properties").getInteger("rogue-jump-boost-amplifier"), TimeUnit.SECONDS.toMillis(45L));

    private final String name;
    private final int energy;
    private final int duration;
    private final int amplifier;
    private final long cooldown;
    Abilities(String name, int energy, int duration, int amplifier, long cooldown) {
        this.name = name;
        this.energy = energy;
        this.duration = duration;
        this.amplifier = amplifier;
        this.cooldown = cooldown;
    }

    public String getName() {
        return name;
    }

    public int getEnergy() {
        return energy;
    }

    public int getDuration() {
        return duration;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public long getCooldown() {
        return cooldown;
    }

    public static Stream<Abilities> stream() {
        return Stream.of(Abilities.values());
    }

    public static Abilities getAbility(String name) {
        return stream().filter(abilities -> abilities.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }
}
