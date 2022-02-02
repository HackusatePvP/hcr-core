package dev.hcr.hcf.utils;

import dev.hcr.hcf.HCF;
import org.bukkit.Bukkit;

public class TaskUtils {

    public static void runSync(Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            Bukkit.getScheduler().runTask(HCF.getPlugin(), runnable);
        }
    }

    public static void runAsync(Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTaskAsynchronously(HCF.getPlugin(), runnable);
        } else {
            runnable.run();
        }
    }
}