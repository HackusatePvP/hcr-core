package dev.hcr.hcf.pvpclass.events;

import dev.hcr.hcf.pvpclass.PvPClass;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public abstract class PvPClassEvent extends Event {
    private final PvPClass pvPClass;
    private final Player player;

    public PvPClassEvent(PvPClass pvPClass, Player player) {
        this.pvPClass = pvPClass;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public PvPClass getPvPClass() {
        return pvPClass;
    }
}
