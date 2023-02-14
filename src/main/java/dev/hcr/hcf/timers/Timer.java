package dev.hcr.hcf.timers;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.pvpclass.structure.Abilities;
import dev.hcr.hcf.timers.events.TimerExpireEvent;
import dev.hcr.hcf.timers.events.TimerStopEvent;
import dev.hcr.hcf.timers.structure.TimerType;
import dev.hcr.hcf.timers.types.PauseTimer;
import dev.hcr.hcf.timers.types.player.*;
import dev.hcr.hcf.timers.types.player.faction.FactionHomeTimer;
import dev.hcr.hcf.users.User;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class Timer extends BukkitRunnable {
    private final String name;
    private final TimerType timerType;
    private Player player;
    private UUID uuid;
    private boolean pause;
    private static final Set<Timer> timers = new HashSet<>();

    public Timer(String name, TimerType timerType) {
        this.name = name;
        this.timerType = timerType;
        if (this instanceof Listener) {
            Bukkit.getPluginManager().registerEvents((Listener) this, HCF.getPlugin());
        }
        timers.add(this);
        this.pause = false;
        this.runTaskTimerAsynchronously(HCF.getPlugin(), 5L, 20L);
    }


    public Timer(Player player, String name) {
        this.name = name;
        this.timerType = TimerType.PLAYER;
        this.player = player;
        if (this instanceof Listener) {
            Bukkit.getPluginManager().registerEvents((Listener) this, HCF.getPlugin());
        }
        User user = User.getUser(player.getUniqueId());
        user.getActiveTimers().add(this);
        this.pause = false;
        this.runTaskTimerAsynchronously(HCF.getPlugin(), 20L, 20L);
    }

    public Timer(UUID uuid, Map<String, Object> map) {
        this.uuid = uuid;
        this.name = (String) map.get("type");
        System.out.println("Timer: " + name);
        this.timerType = TimerType.PLAYER;
        if (this instanceof Listener) {
            Bukkit.getPluginManager().registerEvents((Listener) this, HCF.getPlugin());
        }
        User user = User.getUser(UUID.fromString((String) map.get("uuid")));
        user.getActiveTimers().add(this);
        this.pause = (Boolean) map.get("paused");
        this.runTaskTimerAsynchronously(HCF.getPlugin(), 20L, 20L);
    }

    public String getName() {
        return name;
    }

    public TimerType getTimerType() {
        return timerType;
    }

    public static Timer getTimer(String name) {
        return timers.stream().filter(timer -> timer.getName().equals(name)).findAny().orElse(null);
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public Player getPlayer() {
        return player;
    }

    public UUID getUuid() {
        return uuid;
    }

    public static Set<Timer> getTimers() {
        return timers;
    }

    public abstract String getDisplayName();

    public abstract void run();

    public abstract long getDelay();

    public abstract void setDelay(long delay);

    public abstract long getTimeLeft();

    public abstract boolean isActive();

    public abstract void setActive(boolean active);

    public void save() {
        if (timerType == TimerType.PLAYER) {
            Map<String, Object> map = new HashMap<>();
            if (player == null) {
                map.put("uuid", uuid.toString());
            } else {
                map.put("uuid", player.getUniqueId().toString());
            }
            map.put("type", getName());
            map.put("active", isActive());
            map.put("delay", getDelay());
            map.put("timeLeft", getTimeLeft());
            map.put("paused", isPause());
            if (this instanceof PauseTimer) {
                map.put("pauseMillis", ((PauseTimer) this).getPauseMillis());
            }
            HCF.getPlugin().getStorage().appendTimerDataAsync(map);
        }
    }

    public void end(boolean forced) {
        if (forced) {
            TimerStopEvent event = new TimerStopEvent(this, player);
            Bukkit.getPluginManager().callEvent(event);
        } else {
            TimerExpireEvent event = new TimerExpireEvent(this, player);
            Bukkit.getPluginManager().callEvent(event);
        }
        setDelay(0);
        setActive(false);
        cancel();
        if (timerType == TimerType.PLAYER) {
            User user;
            if (player == null) {
                user = User.getUser(uuid);
            } else {
                user = User.getUser(player.getUniqueId());
            }
            user.getActiveTimers().remove(this);
        }
    }

    public boolean isEffectTimer() {
        return Abilities.stream().filter(abilities -> abilities.getName().equalsIgnoreCase(name)).findAny().orElse(null) != null;
    }

    public String getTimerDisplay() {
        Timer timer = this;
        long duration = timer.getTimeLeft();
        if (timer.isPause() && timer instanceof PauseTimer) {
            long pauseMillis = ((PauseTimer) timer).getPauseMillis();
            duration = timer.getDelay() - pauseMillis;
        }
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration - TimeUnit.HOURS.toMillis(hours));
        if (hours <= 0L) {
            if (minutes <= 0L) {
                return DurationFormatUtils.formatDuration(duration, "ss", true);
            } else {
                return DurationFormatUtils.formatDuration(duration, "mm:ss", true);
            }
        } else {
            return DurationFormatUtils.formatDuration(duration, "hh:mm:ss", true);
        }
    }

    public static Timer createTimerForPlayer(String timer, Player player) {
        switch (timer.toLowerCase()) {
            case "class_warmup":
                return new ClassWarmupTimer(player);
            case "combat":
                return new CombatTimer(player);
            case "enderpearl":
                return new EnderPearlTimer(player);
            case "pvp":
                return new PvPTimer(player);
            case "faction_home":
                return new FactionHomeTimer(player);
            case "archer_resistance":
            case "ability":
                return new EffectTimer(player, Abilities.getAbility(timer));
            case "archer_tag":
                return new ArcherTagTimer(player);
        }
        return null;
    }

    public static Timer createEffectTimer(Player player, Abilities abilities) {
        return new EffectTimer(player, abilities);
    }

    public static Timer createTimer(Map<String, Object> map) {
        String type = (String) map.get("type");
        switch (type.toLowerCase()) {
            case "pvp":
                UUID uuid = UUID.fromString((String) map.get("uuid"));
                Player player = Bukkit.getPlayer(uuid);
                return new PvPTimer(uuid, map);
        }
        return null;
    }
}
