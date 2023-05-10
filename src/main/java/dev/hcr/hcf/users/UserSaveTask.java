package dev.hcr.hcf.users;

import dev.hcr.hcf.HCF;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Saves user data every 30min. Should be ran async.
 */
public class UserSaveTask extends BukkitRunnable {

    @Override
    public void run() {
        HCF.getPlugin().getStorage().saveUsers();
    }
}
