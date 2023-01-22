package dev.hcr.hcf.factions.types.roads;

public class NetherRoad extends RoadFaction {

    public NetherRoad(String name) {
        super(name);
    }

    @Override
    public double getDTRMultiplier() {
        return 1;
    }
}
