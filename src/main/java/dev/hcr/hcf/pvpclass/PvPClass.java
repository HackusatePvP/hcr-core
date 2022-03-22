package dev.hcr.hcf.pvpclass;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.pvpclass.events.ClassUnequippedEvent;
import dev.hcr.hcf.pvpclass.tasks.EffectApplyTask;
import dev.hcr.hcf.pvpclass.tasks.objects.Effect;
import dev.hcr.hcf.users.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class PvPClass {
    private final String name;
    private static final Map<String, PvPClass> classes = new HashMap<>();
    private static final Map<UUID, PvPClass> classTracker = new HashMap<>();
    private static final Map<UUID, EffectApplyTask> effectTaskTracker = new HashMap<>();

    public PvPClass(String name) {
        classes.put(name, this);
        if (this instanceof Listener) {
            Bukkit.getPluginManager().registerEvents((Listener) this, HCF.getPlugin());
        }
        this.name = name;
    }

    public static boolean isClassApplicable(Player player, String pvpClass) {
        PlayerInventory inventory = player.getInventory();
        if (inventory.getHelmet() == null || inventory.getChestplate() == null || inventory.getLeggings() == null || inventory.getBoots() == null) {
            return false;
        }
        switch (pvpClass.toLowerCase()) {
            case "archer":
                return inventory.getHelmet().getType() == Material.LEATHER_HELMET && inventory.getChestplate().getType() == Material.LEATHER_CHESTPLATE && inventory.getLeggings().getType() == Material.LEATHER_LEGGINGS && inventory.getBoots().getType() == Material.LEATHER_BOOTS;
            case "bard":
                return inventory.getHelmet().getType() == Material.GOLD_HELMET && inventory.getChestplate().getType() == Material.GOLD_CHESTPLATE && inventory.getLeggings().getType() == Material.GOLD_LEGGINGS && inventory.getBoots().getType() == Material.GOLD_BOOTS;
            case "rogue":
                break;
            case "miner":
                return inventory.getHelmet().getType() == Material.IRON_HELMET && inventory.getChestplate().getType() == Material.IRON_CHESTPLATE && inventory.getLeggings().getType() == Material.IRON_LEGGINGS && inventory.getBoots().getType() == Material.IRON_BOOTS;
        }
        return false;
    }

    public static PvPClass getApplicableClass(Player player) {
        if (isClassApplicable(player, "archer")) {
            return classes.get("archer");
        } else if (isClassApplicable(player, "miner")) {
            return classes.get("miner");
        } else if (isClassApplicable(player, "bard")) {
            return classes.get("bard");
        } else {
            return null;
        }
    }

    public static boolean hasClassEquipped(Player player) {
        return classTracker.containsKey(player.getUniqueId());
    }

    public static boolean hasClassEquipped(Player player, PvPClass pvPClass) {
        return classTracker.containsKey(player.getUniqueId()) && classTracker.containsValue(pvPClass);
    }

    public static PvPClass getEquippedClass(Player player) {
        return classTracker.get(player.getUniqueId());
    }

    public String getName() {
        return name;
    }

    public abstract String getDisplayName();

    public void equip(Player player) {
        System.out.println("Created tracker for " + player.getName());
        classTracker.put(player.getUniqueId(), this);
        System.out.println("Creating Effect task...");
        createEffectTask(player, getEffects(player));
    }

    public void unequip(Player player) {
        for (Effect effect : getEffects(player)) {
            player.removePotionEffect(effect.getType());
        }
        User user = User.getUser(player.getUniqueId());
        user.setCurrentClass(null);
        System.out.println("Removed tracker for " + player.getName());

        classTracker.remove(player.getUniqueId());

        System.out.println("Ending effect task...");
        EffectApplyTask task = effectTaskTracker.get(player.getUniqueId());
        if (task != null) {
            task.cancel();
        }
        System.out.println("Removing task tracker");
        effectTaskTracker.remove(player.getUniqueId());
    }

    public abstract Effect[] getEffects(Player player);

    public static EffectApplyTask getPlayerEffectTask(Player player) {
        return effectTaskTracker.get(player.getUniqueId());
    }

    public static EffectApplyTask createEffectTask(Player player, Effect... effects) {
        if (effectTaskTracker.containsKey(player.getUniqueId())) return effectTaskTracker.get(player.getUniqueId());
        EffectApplyTask task = new EffectApplyTask(player, effects);
        task.runTaskTimerAsynchronously(HCF.getPlugin(), 5L, 5L);
        effectTaskTracker.put(player.getUniqueId(), task);
        return task;
    }

    public static PvPClass getType(String name) {
        return classes.get(name);
    }

    public static Collection<UUID> getTrackerEntries() {
        return classTracker.keySet();
    }
}
