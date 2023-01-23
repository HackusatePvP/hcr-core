package dev.hcr.hcf.deathbans;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.utils.CC;
import dev.hcr.hcf.utils.backend.ConfigFile;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DeathBan {
    private final UUID uuid;
    private final long executionTime;
    private final long expiredTime;
    private final long duration;

    private static final Map<UUID, DeathBan> activeDeathbans = new HashMap<>();

    public DeathBan(UUID uuid, long duration) {
        this.uuid = uuid;
        this.executionTime = System.currentTimeMillis();
        this.duration = duration;
        this.expiredTime = System.currentTimeMillis() + duration;
        activeDeathbans.put(uuid, this);

        HCF.getPlugin().getStorage().saveDeathBan(this);
    }

    public DeathBan(Map<String, Object> map) {
        this.uuid = (UUID) map.get("uuid");
        this.executionTime = (Long) map.get("executionTime");
        this.duration = (Long) map.get("duration");
        this.expiredTime = (Long) map.get("expiredTime");

        activeDeathbans.put(uuid, this);
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("uuid", uuid);
        map.put("executionTime", executionTime);
        map.put("duration", duration);
        map.put("expiredTime", expiredTime);
        return map;
    }

    public UUID getUniqueID() {
        return uuid;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public long getDuration() {
        return duration;
    }

    public long getExpiredTime() {
        return expiredTime;
    }

    public long getTimeLeft() {
        return expiredTime - System.currentTimeMillis();
    }

    public void execute(Player player) {
        player.kickPlayer(CC.translate("&cYou are now deathbanned for:" +
                "\n&c" + DurationFormatUtils.formatDurationWords(getTimeLeft(), true, true) +
                "\n&cYou can buy lives @ www.hcr.dev"));
    }

    public void complete() {
        HCF.getPlugin().getStorage().removeDeathBan(uuid);
        activeDeathbans.remove(uuid);
    }

    /* All static methods below */
    public static DeathBan getActiveDeathBan(UUID uuid) {
        return activeDeathbans.get(uuid);
    }

    // Hyful rank1
    public static long getRankDuration(String rank) {
        ConfigFile config = HCF.getPlugin().getConfiguration("config");
        int minutes;
        if (config.getConfiguration().contains("deathbans." + rank)) {
            minutes = config.getInt("deathbans." + rank);
        } else {
            minutes = config.getInt("deathbans.default");
        }
        return TimeUnit.MINUTES.toMillis(minutes);
    }

}
