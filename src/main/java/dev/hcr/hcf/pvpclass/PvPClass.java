package dev.hcr.hcf.pvpclass;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.pvpclass.tasks.EffectRestoreTask;
import dev.hcr.hcf.pvpclass.tasks.EnergyBuildTask;
import dev.hcr.hcf.pvpclass.tasks.PassiveEffectApplyTask;
import dev.hcr.hcf.pvpclass.types.bard.BardClass;
import dev.hcr.hcf.pvpclass.types.bard.objects.Effect;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.backend.types.PropertiesConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public abstract class PvPClass {
    private final String name;
    private static final Map<String, PvPClass> classes = new HashMap<>();
    private static final Map<UUID, PvPClass> classTracker = new HashMap<>();
    private static final Map<UUID, EnergyBuildTask> energyTracker = new HashMap<>();

    private final boolean debug = PropertiesConfiguration.getPropertiesConfiguration("hcf.properties").getBoolean("debug");

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
                return inventory.getHelmet().getType() == Material.CHAINMAIL_HELMET && inventory.getChestplate().getType() == Material.CHAINMAIL_CHESTPLATE && inventory.getLeggings().getType() == Material.CHAINMAIL_LEGGINGS && inventory.getBoots().getType() == Material.CHAINMAIL_BOOTS;
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
        } else if (isClassApplicable(player, "rogue")) {
            return classes.get("rogue");
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
        classTracker.put(player.getUniqueId(), this);
        if (debug) {
            System.out.println("Created tracker for " + player.getName());
            System.out.println("Creating Effect task...");
        }

        PassiveEffectApplyTask applyTask = new PassiveEffectApplyTask(player, this);
        applyTask.runTaskTimer(HCF.getPlugin(), 20L, 5L);

        if (this instanceof BardClass) {
            EnergyBuildTask energyBuildTask = new EnergyBuildTask(player);
            energyBuildTask.runTaskTimerAsynchronously(HCF.getPlugin(), 20L, 20L);
            energyTracker.put(player.getUniqueId(), energyBuildTask);
        }
    }

    public void unequip(Player player) {
        for (Effect effect : getEffects(player)) {
            player.removePotionEffect(effect.getType());
        }
        User user = User.getUser(player.getUniqueId());
        user.setCurrentClass(null);

        classTracker.remove(player.getUniqueId());

        System.out.println("Removed tracker for " + player.getName());
    }

    public void applyEffect(Player player, PotionEffect effect) {
        if (debug) {
            System.out.println("Applying effect " + effect.getType().getName() + " to " + player.getName());
        }
        Faction otherFaction = Faction.getByLocation(player.getLocation());
        if (otherFaction != null && !otherFaction.isDeathBan()) {
            if (debug) {
                System.out.println("Could not validate skipping.");
            }
            return;
        }

        if (canOverrideLevel(player, effect) && player.hasPotionEffect(effect.getType())) {
            if (debug) {
                System.out.println("Attempting to override effect...");
            }
            PotionEffect temp = player.getActivePotionEffects().stream().filter(potionEffect -> potionEffect.getType().getName().equals(effect.getType().getName())).findFirst().orElse(null);
            if (temp == null) {
                if (debug) {
                    System.out.println("Returned null skipping effect...");
                }
                return;
            }

            if (temp.getDuration() > 100) {
                if (debug) {
                    System.out.println("Duration is greater then 100 scheduling task...");
                }
                PotionEffect pre = new PotionEffect(temp.getType(), temp.getDuration(), temp.getAmplifier(), temp.isAmbient());
                new EffectRestoreTask(player, pre).runTaskLater(HCF.getPlugin(), effect.getDuration() - 5);
            }
        }

        if (canOverrideLevel(player, effect)) {
            if (debug) {
                System.out.println("Effect can override again?");
            }
            player.addPotionEffect(effect, true);
        }
        if (debug) {
            System.out.println("Done!");
        }
    }

    public boolean canOverrideLevel(Player player, PotionEffect effect) {
        if(player.hasPotionEffect(effect.getType())) {
            PotionEffect before = player.getActivePotionEffects().stream().filter(potionEffect -> potionEffect.getType().getId() == effect.getType().getId()).findFirst().orElse(null);
            if(before == null)
                return true;

            return before.getAmplifier() < effect.getAmplifier() || (before.getAmplifier() == effect.getAmplifier() && before.getDuration() < effect.getDuration() && !effect.getType().equals(PotionEffectType.REGENERATION));
        }

        return true;
    }


    public abstract Effect[] getEffects(Player player);

    public EnergyBuildTask getEnergyTracker(Player player) {
        return energyTracker.get(player.getUniqueId());
    }


    public static PvPClass getType(String name) {
        return classes.get(name);
    }

    public static Collection<UUID> getTrackerEntries() {
        return classTracker.keySet();
    }
}
