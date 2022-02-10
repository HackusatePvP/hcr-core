package dev.hcr.hcf.commands.donor;

import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.TaskUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Random;

public class ClaimBonusChestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        User user = User.getUser(player.getUniqueId());
        if (user.hasClaimedChest()) {
            player.sendMessage(ChatColor.RED + "You already claimed your chest for this map.");
            return true;
        }
        giveBonusChestItems(player.getInventory());
        return false;
    }

    private void giveBonusChestItems(PlayerInventory inventory) {
        TaskUtils.runAsync(() -> {
            Random random = new Random();
            int items = 0;
            do {
                int randomItem = random.nextInt(15) + 1;
                int stackSize = random.nextInt(3) + 1;
                items++;
                ItemStack itemStack = generateRandomItem(randomItem, stackSize);
                int slot = 0;
                boolean set = false;
                do {
                    ItemStack slotItem = inventory.getItem(slot);
                    if (slotItem == null || slotItem.getType() == Material.AIR) {
                        inventory.setItem(slot, itemStack);
                        set = true;
                    }
                    slot++;
                } while (!set);
            } while (items < 10);
        });
    }

    private ItemStack generateRandomItem(int random, int stackSize) {
        if (stackSize == 0) {
            stackSize = 1;
        }
        switch (random) {
            case 1:
                return new ItemStack(Material.APPLE, stackSize);
            case 2:
                return new ItemStack(Material.STICK, stackSize);
            case 3:
                return new ItemStack(Material.WOOD_PICKAXE, 1);
            case 4:
                return new ItemStack(Material.WOOD_AXE, 1);
            case 5:
                return new ItemStack(Material.WOOD_SWORD, 1);
            case 6:
                return new ItemStack(Material.LOG, stackSize);
            case 7:
                return new ItemStack(Material.LOG, stackSize, (short) 1);
            case 8:
                return new ItemStack(Material.LOG, stackSize, (short) 2);
            case 9:
                return new ItemStack(Material.LOG, stackSize, (short) 3);
            case 10:
                return new ItemStack(Material.LOG_2, stackSize);
            case 11:
                return new ItemStack(Material.BREAD, stackSize);
            case 12:
                return new ItemStack(Material.SAPLING, stackSize);
            case 13:
                return new ItemStack(Material.SAPLING, stackSize, (short) 1);
            case 14:
                return new ItemStack(Material.SAPLING, stackSize, (short) 2);
            default:
            case 15:
                return new ItemStack(Material.SAPLING, stackSize, (short) 3);
        }
    }
}
