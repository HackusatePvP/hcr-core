package dev.hcr.hcf.users.statistics.types;

import dev.hcr.hcf.users.User;
import dev.hcr.hcf.users.statistics.UserStatistics;
import org.bson.Document;

public class PvPStatistics extends UserStatistics {
    private final User user;
    private int kills, deaths, killstreak;

    public PvPStatistics(User user) {
        super(user);
        this.user = user;
        this.kills = 0;
        this.deaths = 0;
        this.killstreak = 0;
        getStatisticKeyMapping().put("kills", 0);
        getStatisticKeyMapping().put("deaths", 0);
        getStatisticKeyMapping().put("killstreak", 0);
    }

    public PvPStatistics(User user, Document document) {
        super(user);
        this.user = user;
        if (document.containsKey("kills")) {
            this.kills = document.getInteger("kills");
        }
        if (document.containsKey("deaths")) {
            this.deaths = document.getInteger("deaths");
        }
        if (document.containsKey("killstreak")) {
            this.killstreak = document.getInteger("killstreak");
        }
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getKillstreak() {
        return killstreak;
    }

    public void incrementKills() {
        kills++;
    }

    public void incrementDeaths() {
        deaths++;
    }

    public void incrementKillstreak() {
        killstreak++;
    }

    public void resetKillstreak() {
        killstreak = 0;
    }
}
