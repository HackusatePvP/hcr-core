package dev.hcr.hcf.pvpclass.events;

import dev.hcr.hcf.pvpclass.PvPClass;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class ClassUnequippedEvent extends PvPClassEvent {
    private static final HandlerList handlers = new HandlerList();

    public ClassUnequippedEvent(PvPClass pvPClass, Player player) {
        super(pvPClass, player);
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
