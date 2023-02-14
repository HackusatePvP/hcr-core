package dev.hcr.hcf.timers.types;

import dev.hcr.hcf.timers.Timer;
import dev.hcr.hcf.timers.structure.TimerType;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public abstract class PauseTimer extends Timer {

    public PauseTimer(Player player, String name) {
        super(player, name);
    }

    public PauseTimer(String name, TimerType timerType) {
        super(name, timerType);
    }

    public PauseTimer(UUID uuid, Map<String, Object> map) {
        super(uuid, map);
        setPauseMillis(((Number) map.get("pauseMillis")).longValue());
    }

    public abstract void reset();

    public abstract long getPauseMillis();

    public abstract void setPauseMillis(long time);

}
