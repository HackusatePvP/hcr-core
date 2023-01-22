package dev.hcr.hcf.users.statistics;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserStatistics {
    private final UUID uuid;
    private int kills, deaths;

    private final Map<String, Integer> oreStats = new HashMap<>();

    public UserStatistics(UUID uuid) {
        this.uuid = uuid;
        this.kills = 0;
        this.deaths = 0;
        oreStats.put("emerald", 0);
        oreStats.put("diamond", 0);
        oreStats.put("gold", 0);
        oreStats.put("lapis", 0);
        oreStats.put("redstone", 0);
        oreStats.put("iron", 0);
        oreStats.put("coal", 0);
    }

    public UserStatistics(UUID uuid, Map<String, Object> map) {
        this.uuid = uuid;
        this.kills = (Integer) map.get("kills");
        this.deaths = (Integer) map.get("deaths");
        String oreString = (String) map.get("oreStats");
        // emerald diamond gold lapis redstone iron coal
        String[] ores = oreString.split("@");
        oreStats.put("emerald", Integer.parseInt(ores[0]));
        oreStats.put("diamond", Integer.parseInt(ores[1]));
        oreStats.put("gold", Integer.parseInt(ores[2]));
        oreStats.put("lapis", Integer.parseInt(ores[3]));
        oreStats.put("redstone", Integer.parseInt(ores[4]));
        oreStats.put("iron", Integer.parseInt(ores[5]));
        oreStats.put("coal", Integer.parseInt(ores[6]));
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("kills", kills);
        map.put("deaths", deaths);
        StringBuilder builder = new StringBuilder();
        for (String s : oreStats.keySet()) {
            System.out.println("S: " + s);
            builder.append(oreStats.get(s)).append("@");
        }
        System.out.println("Ore Stats: " + builder);
        map.put("oreStats", builder.toString());
        return map;
    }

    public UUID getUniqueID() {
        return uuid;
    }

    public int getKills() {
        return kills;
    }

    public void incrementKills() {
        kills++;
    }

    public int getDeaths() {
        return deaths;
    }

    public void incrementDeaths() {
        deaths++;
    }

    public int getOreStat(String ore) {
        return oreStats.get(ore.toLowerCase());
    }

    public void incrementOre(String ore) {
        int stat = oreStats.get(ore.toLowerCase());
        stat++;
        oreStats.put(ore.toLowerCase(), stat);
    }
}
