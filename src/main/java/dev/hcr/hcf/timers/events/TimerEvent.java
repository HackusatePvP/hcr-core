package dev.hcr.hcf.timers.events;

import dev.hcr.hcf.timers.Timer;
import org.bukkit.event.Event;

public abstract class TimerEvent extends Event {
    private final Timer timer;

    public TimerEvent(Timer timer) {
        this.timer = timer;
    }

    public Timer getTimer() {
        return timer;
    }
}
