package dev.hcr.hcf.users.statistics.types;

import dev.hcr.hcf.users.User;
import dev.hcr.hcf.users.statistics.UserStatistics;
import dev.hcr.hcf.users.statistics.types.enums.Ores;
import org.bson.Document;

public class OreStatistics extends UserStatistics {
    private final User user;
    private int diamonds, emeralds, gold, lapis, redstone, iron, coal;

    public OreStatistics(User user) {
        super(user);
        this.user = user;
        this.diamonds = 0;
        this.emeralds = 0;
        this.gold = 0;
        this.lapis = 0;
        this.redstone = 0;
        this.iron = 0;
        this.coal = 0;
        getStatisticKeyMapping().put("diamonds", 0);
        getStatisticKeyMapping().put("emeralds", 0);
        getStatisticKeyMapping().put("gold", 0);
    }

    public OreStatistics(User user, Document document) {
        super(user);
        this.user = user;
        if (document.containsKey("diamonds")) {
            this.diamonds = document.getInteger("diamonds");
            getStatisticKeyMapping().put("diamonds", emeralds);
        }
        if (document.containsKey("emeralds")) {
            this.emeralds = document.getInteger("emeralds");
            getStatisticKeyMapping().put("emeralds", emeralds);
        }
        if (document.containsKey("gold")) {
            this.gold = document.getInteger("gold");
            getStatisticKeyMapping().put("gold", gold);
        }
        if (document.containsKey("lapis")) {
            this.lapis = document.getInteger("lapis");
            getStatisticKeyMapping().put("lapis", lapis);
        }
        if (document.containsKey("redstone")) {
            this.redstone = document.getInteger("redstone");
            getStatisticKeyMapping().put("redstone", emeralds);
        }
        if (document.containsKey("iron")) {
            this.iron = document.getInteger("iron");
            getStatisticKeyMapping().put("iron", iron);
        }
        if (document.containsKey("coal")) {
            this.coal = document.getInteger("coal");
            getStatisticKeyMapping().put("coal", emeralds);
        }
    }

    public Integer getValue(Ores ore) {
        switch (ore) {
            case DIAMONDS:
                return diamonds;
            case EMERALDS:
                return emeralds;
            case GOLD:
                return gold;
            case LAPIS:
                return lapis;
            case REDSTONE:
                return redstone;
            case IRON:
                return iron;
            case COAL:
                return coal;
        }
        return 0;
    }

    public void incrementOreStatistic(Ores ore) {
        switch (ore) {
            case DIAMONDS:
                set("diamonds", user.getOreStatistics().getValue(Ores.DIAMONDS) + 1);
            case EMERALDS:
                set("emeralds", user.getOreStatistics().getValue(Ores.EMERALDS) + 1);
            case GOLD:
                set("gold", user.getOreStatistics().getValue(Ores.GOLD) + 1);
            case LAPIS:
                set("lapis", user.getOreStatistics().getValue(Ores.LAPIS) + 1);
            case REDSTONE:
                set("redstone", user.getOreStatistics().getValue(Ores.REDSTONE) + 1);
            case IRON:
                set("iron", user.getOreStatistics().getValue(Ores.IRON) + 1);
            case COAL:
                set("coal", user.getOreStatistics().getValue(Ores.COAL) + 1);
        }
    }

    @Override
    public void set(String key, Object value) {
        super.set(key, value);
    }
}
