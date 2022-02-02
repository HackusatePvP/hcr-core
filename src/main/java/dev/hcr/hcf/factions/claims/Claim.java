package dev.hcr.hcf.factions.claims;

import dev.hcr.hcf.factions.claims.cuboid.Cuboid;

public class Claim {
    private final Cuboid cuboid;
    private final String name;
    private double value;

    public Claim(String name, Cuboid cuboid) {
        this.cuboid = cuboid;
        this.name = name;
    }

    public Claim(String name) {
        this.name = name;
        this.cuboid = null;
    }

    public String getName() {
        return name;
    }

    public Cuboid getCuboid() {
        return cuboid;
    }

    public double getValue() {
        return value;
    }
}
