package dev.hcr.hcf.factions.events.members;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.events.FactionEvent;
import dev.hcr.hcf.factions.types.PlayerFaction;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class FactionAllyAttackEvent extends FactionEvent implements Cancellable {
    private boolean cancelled = false;
    private final PlayerFaction ally;
    private final Player attacker;
    private final Player defender;

    private static final HandlerList handlers = new HandlerList();

    public FactionAllyAttackEvent(Faction faction, PlayerFaction ally, Player attacker, Player defender) {
        super(faction);
        this.ally = ally;
        this.attacker = attacker;
        this.defender = defender;
    }

    public PlayerFaction getAlly() {
        return ally;
    }

    public Player getAttacker() {
        return attacker;
    }

    public Player getDefender() {
        return defender;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
