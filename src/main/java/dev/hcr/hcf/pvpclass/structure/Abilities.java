package dev.hcr.hcf.pvpclass.structure;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public enum Abilities {
    ARCHER_SPEED("archer_speed", TimeUnit.SECONDS.toMillis(15L)),
    ARCHER_RESISTANCE("archer_resistance", TimeUnit.SECONDS.toMillis(25L)),

    BARD_SPEED("bard_speed", TimeUnit.SECONDS.toMillis(20L)),
    BARD_RESISTANCE("bard_resistance", TimeUnit.SECONDS.toMillis(20L)),
    BARD_STRENGTH("bard_strength", TimeUnit.SECONDS.toMillis(20L)),
    BARD_REGENERATION("bard_regeneration", TimeUnit.SECONDS.toMillis(20L)),
    BARD_JUMP_BOOST("bard_jump_boost", TimeUnit.SECONDS.toMillis(20L)),
    BARD_WITHER("bard_wither", TimeUnit.SECONDS.toMillis(20L));

    private final String name;
    private final long cooldown;
    Abilities(String name, long cooldown) {
        this.name = name;
        this.cooldown = cooldown;
    }

    public String getName() {
        return name;
    }

    public long getCooldown() {
        return cooldown;
    }

    public static Stream<Abilities> stream() {
        return Stream.of(Abilities.values());
    }
}
