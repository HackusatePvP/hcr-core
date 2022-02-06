package dev.hcr.hcf.listeners.player;

import dev.hcr.hcf.users.User;
import dev.hcr.hcf.users.statistics.types.enums.Ores;
import org.bukkit.block.Block;
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
                user.getOreStatistics().incrementOreStatistic(Ores.DIAMONDS);
                break;
            case EMERALD_ORE:
                user.getOreStatistics().incrementOreStatistic(Ores.EMERALDS);
                break;
            case GOLD_ORE:
                user.getOreStatistics().incrementOreStatistic(Ores.GOLD);
                break;
            case LAPIS_ORE:
                user.getOreStatistics().incrementOreStatistic(Ores.LAPIS);
                break;
            case REDSTONE_ORE:
                user.getOreStatistics().incrementOreStatistic(Ores.REDSTONE);
                break;
            case IRON_ORE:
                user.getOreStatistics().incrementOreStatistic(Ores.IRON);
                break;
            case COAL_ORE:
                user.getOreStatistics().incrementOreStatistic(Ores.COAL);
                break;
        }
    }
}
