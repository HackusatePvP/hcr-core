package dev.hcr.hcf.utils.client.lunar;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.nethandler.shared.LCPacketWaypointAdd;
import com.lunarclient.bukkitapi.nethandler.shared.LCPacketWaypointRemove;
import dev.hcr.hcf.utils.client.ClientPlayerPacket;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class LunarClientPlayerPacket extends ClientPlayerPacket {
    private final Player player;

    private static final Map<Player, Map.Entry<String, World>> waypointTracker = new HashMap<>(); // Used to track waypoints sent making it easier to remove them later.

    public LunarClientPlayerPacket(Player player) {
        super(player);
        this.player = player;
    }

    public void sendWaypoint(String name, int color, Location location, boolean forced) {
        // get previous waypoints from the player
        if (waypointTracker.containsKey(player)) {
            String toRemove = waypointTracker.get(player).getKey();
            World world = waypointTracker.get(player).getValue();
            if (toRemove != null && world != null) {
                // Delete the found entry.
                new LCPacketWaypointRemove(name, world.getName());
            }
        }
        new LCPacketWaypointAdd(name, location.getWorld().getName(), color, location.getBlockX(), location.getBlockY(), location.getBlockZ(), forced, true);
        waypointTracker.put(player, new AbstractMap.SimpleEntry<>(name, location.getWorld()));
    }
}
