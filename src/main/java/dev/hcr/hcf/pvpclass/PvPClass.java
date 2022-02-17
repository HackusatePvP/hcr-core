package dev.hcr.hcf.pvpclass;

import dev.hcr.hcf.HCF;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

public abstract class PvPClass {
    private static final Map<String, PvPClass> classes = new HashMap<>();

    public PvPClass(String name) {
        classes.put(name, this);
        if (this instanceof Listener) {
            Bukkit.getPluginManager().registerEvents((Listener) this, HCF.getPlugin());
        }
    }

    public static boolean hasClassEquipped(Player player, String pvpClass) {
        PlayerInventory inventory = player.getInventory();
        if (inventory.getHelmet() == null || inventory.getChestplate() == null || inventory.getLeggings() == null || inventory.getBoots() == null) {
            return false;
        }
        switch (pvpClass.toLowerCase()) {
            case "archer":
                return inventory.getHelmet().getType() == Material.LEATHER_HELMET && inventory.getChestplate().getType() == Material.LEATHER_CHESTPLATE && inventory.getLeggings().getType() == Material.LEATHER_LEGGINGS && inventory.getBoots().getType() == Material.LEATHER_BOOTS;
            case "bard":
                break;
            case "rogue":
                break;
            case "miner":
                break;
        }

        return false;
    }

    public static PvPClass getEquippedClass(Player player) {
        PlayerInventory inventory = player.getInventory();
        if (inventory.getHelmet() == null || inventory.getChestplate() == null || inventory.getLeggings() == null || inventory.getBoots() == null) {
            return null;
        }
        if (hasClassEquipped(player, "archer")) {
            return classes.get("archer");
        } else if (hasClassEquipped(player, "bard")) {
            classes.get("bard");
        } else if (hasClassEquipped(player, "rogue")) {
            classes.get("rogue");
        } else if (hasClassEquipped(player, "miner")) {
            classes.get("miner");
        }
        return null;
    }

    public abstract void equip(Player player);

    public abstract void unequip(Player player);

    public static PvPClass getType(String name) {
        return classes.get(name);
    }

}
