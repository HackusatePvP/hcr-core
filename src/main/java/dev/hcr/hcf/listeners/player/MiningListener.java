package dev.hcr.hcf.listeners.player;

import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class MiningListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        User user = User.getUser(player.getUniqueId());
        Block block = event.getBlock();
        switch (block.getType()) {
            case DIAMOND_ORE:
                user.getUserStatistics().incrementOre("diamond");

                // Do FD broadcast I guess
                int diamonds = 0;
                for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
                    if (entity instanceof Block) {
                        Block found = (Block) entity;
                        if (found.getType() == Material.DIAMOND_ORE) {
                            diamonds++;
                        }
                    }
                }
                Bukkit.broadcast(CC.translate("&7[&bDF&7] &r" + player.getName() + " has found &b" + diamonds + " &rdiamonds."), "");
                break;
            case EMERALD_ORE:
                user.getUserStatistics().incrementOre("emerald");
                break;
            case GOLD_ORE:
                user.getUserStatistics().incrementOre("gold");
                break;
            case LAPIS_ORE:
                user.getUserStatistics().incrementOre("lapis");
                break;
            case REDSTONE_ORE:
                user.getUserStatistics().incrementOre("redstone");
                break;
            case IRON_ORE:
                user.getUserStatistics().incrementOre("iron");
                break;
            case COAL_ORE:
                user.getUserStatistics().incrementOre("coal");
                break;
        }
    }
}
