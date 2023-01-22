package dev.hcr.hcf.pvpclass.types.bard;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.factions.types.SafeZoneFaction;
import dev.hcr.hcf.pvpclass.PvPClass;
import dev.hcr.hcf.pvpclass.events.ClassUnequippedEvent;
import dev.hcr.hcf.pvpclass.structure.Abilities;
import dev.hcr.hcf.pvpclass.tasks.EnergyBuildTask;
import dev.hcr.hcf.pvpclass.types.bard.objects.Effect;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BardClass extends PvPClass implements Listener {
    private final Map<UUID, EnergyBuildTask> energyTask = new HashMap<>();

    public BardClass() {
        super("bard");
    }

    @Override
    public String getDisplayName() {
        return "&6Bard";
    }

    @Override
    public void equip(Player player) {
        EnergyBuildTask task = new EnergyBuildTask(player);
        task.runTaskTimerAsynchronously(HCF.getPlugin(), 20L, 20L);
        energyTask.put(player.getUniqueId(), task);
        super.equip(player);
    }

    @Override
    public void unequip(Player player) {
        EnergyBuildTask task = energyTask.get(player.getUniqueId());
        if (task != null) {
            task.cancel();
        }
        super.unequip(player);
    }

    @Override
    public Effect[] getEffects(Player player) {
        return new Effect[]{
                new Effect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1), true),
                new Effect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1), true),
                new Effect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 1), true)
        };
    }

    public ItemStack[] getClickableEffects() {
        return new ItemStack[]{
                new ItemStack(Material.SUGAR),
                new ItemStack(Material.IRON_INGOT),
                new ItemStack(Material.BLAZE_POWDER),
                new ItemStack(Material.FEATHER),
                new ItemStack(Material.GHAST_TEAR),
                new ItemStack(Material.SPIDER_EYE)
        };
    }


    public Effect getHeldEffect(ItemStack itemStack) {
        switch (itemStack.getType()) {
            case SUGAR:
                return new Effect(new PotionEffect(PotionEffectType.SPEED, 20 * 5, 1), false, false, true); // Do not self apply holding effects that conflict with passives
            case IRON_INGOT:
                return new Effect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 0), false, false, true);
            case GHAST_TEAR:
                return new Effect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 1), false, false, true);
            case BLAZE_POWDER:
                return new Effect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 5, 0), false, false, true);
            case FEATHER:
                return new Effect(new PotionEffect(PotionEffectType.JUMP, 20 * 5, 3), false, false, true);
            case MAGMA_CREAM:
                return new Effect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 5, 0), false, false, true);
        }
        return null;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        User user = User.getUser(player.getUniqueId());
        if (user.getCurrentClass() != null) {
            for (Effect effect : user.getCurrentClass().getEffects(player)) {
                player.removePotionEffect(effect.getType());
            }
            ClassUnequippedEvent unequippedEvent = new ClassUnequippedEvent(this, player);
            Bukkit.getPluginManager().callEvent(unequippedEvent);
            user.setCurrentClass(null);
        }
    }

    @EventHandler
    public void onPlayerHoldItem(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if (!hasClassEquipped(player, this)) return;
        User user = User.getUser(player.getUniqueId());
        if (user.hasActiveTimer("pvp")) return;
        Faction locationFaction = Faction.getByLocation(player.getLocation());
        if (locationFaction instanceof SafeZoneFaction) return;
        ItemStack nextItem = player.getInventory().getItem(event.getNewSlot());
        if (nextItem == null) return;
        Effect effect = getHeldEffect(nextItem);
        if (effect != null) {
            applyHeldBard(player, getHeldEffect(nextItem));
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!hasClassEquipped(event.getPlayer(), this)) {
            return;
        }
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }
        if (!event.hasItem()) {
            return;
        }
        ItemStack item = event.getItem();
        Material itemType = event.getItem().getType();
        if (Arrays.stream(getClickableEffects()).noneMatch(itemStack -> itemStack.getType() == itemType)) return;
        Abilities ability;

        Player player = event.getPlayer();
        User user = User.getUser(player.getUniqueId());
        Faction otherFaction = Faction.getByLocation(player.getLocation());
        if (otherFaction != null && !otherFaction.isDeathBan()) {
            //player.sendMessageBARD_SAFEZONE.toString());
            return;
        }
        if (user.hasActiveTimer("pvp")) {
            player.sendMessage(ChatColor.RED + "You cannot use bard effects whilst PvPTimer is active.");
        }
        switch (itemType) {
            case SUGAR:
                ability = Abilities.BARD_SPEED;
                if (user.hasActiveTimer(ability)) {
                    player.sendMessage(ChatColor.RED + "You are on speed cooldown.");
                    return;
                }
                if (applyEffectWithEnergy(player, item, ability)) {
                    handleItemAmount(item);
                    user.setTimer(ability, true);
                }
                break;
            case IRON_INGOT:
                ability = Abilities.BARD_RESISTANCE;
                if (user.hasActiveTimer(ability)) {
                    player.sendMessage(ChatColor.RED + "You are on resistance cooldown.");
                    return;
                }
                if (applyEffectWithEnergy(player, item, ability)) {
                    handleItemAmount(item);
                    user.setTimer(ability, true);
                }
                break;
            case BLAZE_POWDER:
                ability = Abilities.BARD_STRENGTH;
                if (user.hasActiveTimer(ability)) {
                    player.sendMessage(ChatColor.RED + "You are on strength cooldown.");
                    return;
                }
                if (applyEffectWithEnergy(player, item, ability)) {
                    handleItemAmount(item);
                    user.setTimer(ability, true);
                }
                break;
            case FEATHER:
                ability = Abilities.BARD_JUMP_BOOST;
                if (user.hasActiveTimer(ability)) {
                    player.sendMessage(ChatColor.RED + "You are on jump boost cooldown.");
                    return;
                }
                if (applyEffectWithEnergy(player, item, ability)) {
                    handleItemAmount(item);
                    user.setTimer(ability, true);
                }
                break;
            case GHAST_TEAR:
                ability = Abilities.BARD_REGENERATION;
                if (user.hasActiveTimer(ability)) {
                    player.sendMessage(ChatColor.RED + "You are on regeneration cooldown.");
                    return;
                }
                if (applyEffectWithEnergy(player, item, ability)) {
                    handleItemAmount(item);
                    user.setTimer(ability, true);
                }
                break;
            case SPIDER_EYE:
                ability = Abilities.BARD_WITHER;
                if (user.hasActiveTimer(ability)) {
                    player.sendMessage(ChatColor.RED + "You are on wither cooldown.");
                    return;
                }
                if (applyEffectWithEnergy(player, item, ability)) {
                    handleItemAmount(item);
                    user.setTimer(ability, true);
                }
                break;
        }
    }

    private boolean applyEffectWithEnergy(Player player, ItemStack item, Abilities ability) {
        System.out.println("Applying effect!");
        if (item != null) {
            if (ability == null) return false;
            //System.out.println("Passed first validation...");
            int energyToConsume = ability.getEnergy();
            EnergyBuildTask energyBuildTask = getEnergyTracker(player);
            int energy = energyBuildTask.getEnergy();
            if (energy < energyToConsume) {
                //System.out.println("Player does not have enough energy!");
                player.sendMessage(CC.translate("&cYou do not have enough energy: " + energy + "&7/&c" + energyToConsume));
                return false;
            }
            //System.out.println(ability.getName());
            Effect effect = getAbilityEffect(ability);
            if (effect == null) {
                //System.out.println("Could not translate an effect given the ability!");
                return false;
            }
            //System.out.println("applying effects again?");
            applyEffects(player, effect);
            energyBuildTask.removeEnergy(energyToConsume);
            player.sendMessage("You have used: " + effect.getEffect().getType().getName() + " Costing you: " + energyToConsume + " energy.");
            return true;
        }

        return false;
    }

    private Effect getAbilityEffect(Abilities ability) {
        switch (ability) {
            case BARD_SPEED:
                return new Effect(new PotionEffect(PotionEffectType.SPEED, ability.getDuration(), ability.getAmplifier()), false, true, false);
            case BARD_STRENGTH:
                return new Effect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, ability.getDuration(), ability.getAmplifier()), false, true, false);
            case BARD_REGENERATION:
                return new Effect(new PotionEffect(PotionEffectType.REGENERATION, ability.getDuration(), ability.getAmplifier()), false, true, false);
            case BARD_RESISTANCE:
                return new Effect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, ability.getDuration(), ability.getAmplifier()), false, true, false);
            case BARD_JUMP_BOOST:
                return new Effect(new PotionEffect(PotionEffectType.JUMP, ability.getDuration(), ability.getAmplifier()), false, true, false);
            case BARD_WITHER:
                return new Effect(new PotionEffect(PotionEffectType.WITHER, ability.getDuration(), ability.getAmplifier()), false, true, false);
        }
        return null;
    }

    private void handleItemAmount(ItemStack itemStack) {
        int amount = itemStack.getAmount();
        int updatedAmount = amount - 1;
        if (updatedAmount == 0) {
            itemStack.setType(Material.AIR);
        } else {
            itemStack.setAmount(updatedAmount);
        }
    }

    public void applyHeldBard(Player player, Effect effect) {
        if (effect != null) applyEffects(player, effect);
    }

    public void applyHeldBard(Player player, ItemStack item) {
        if (item != null) {
            if (getHeldEffect(item) != null) {
                applyEffects(player, getHeldEffect(item));
            }
        }
    }

    private void applyEffects(Player player, Effect effect) {
        User user = User.getUser(player.getUniqueId());
        if (user.hasActiveTimer("pvp")) {
            player.sendMessage(ChatColor.RED + "You cannot give effects whilst PvPTimer is active.");
            return;
        }
        PlayerFaction faction = (PlayerFaction) user.getFaction();
        int range = 25;
        if (faction != null) {
            for (Entity entity : player.getNearbyEntities(range, range, range)) {
                if (!(entity instanceof Player)) {
                    continue;
                }
                Player found = (Player) entity;
                User target = User.getUser(found.getUniqueId());
                if (target.hasTimer("pvp")) continue;
                if (effect.isDebuff()) {
                    if (target.hasFaction() && (target.getFaction().getUniqueID() == faction.getUniqueID()) || faction.hasAlly(target.getFaction())) {
                        continue;
                    }
                } else {
                    if (!target.hasFaction()) {
                        continue;
                    }
                    if (!faction.hasMember(target.getUniqueID())) {
                        continue;
                    }
                    if (Faction.getByLocation(player.getLocation()) instanceof SafeZoneFaction || Faction.getByLocation(found.getLocation()) instanceof SafeZoneFaction) {
                        continue;
                    }
                }
                applyEffect(found, effect.getEffect());
            }
        }

        if (!effect.isSelfApply()) {
            return;
        }

        applyEffect(player, effect.getEffect());
    }

    private void removeEffects(Player player, PotionEffect effect) {
        User user = User.getUser(player.getUniqueId());
        PlayerFaction faction = (PlayerFaction) user.getFaction();
        int range = 25;
        if (faction != null) {
            for (Entity entity : player.getNearbyEntities(range, range, range)) {
                if (!(entity instanceof Player)) {
                    continue;
                }
                Player p = (Player) entity;
                User target = User.getUser(player.getUniqueId());
                if (!target.hasFaction())
                    continue;
                if (!(target.getFaction().getUniqueID() == faction.getUniqueID()) && !faction.hasAlly(target.getFaction())) {
                    continue;
                }
                removeEffect(p, effect);
            }
        }

        removeEffect(player, effect);
    }

    private void removeEffect(Player player, PotionEffect effect) {
        if (!player.hasPotionEffect(effect.getType())) {
            return;
        }
        PotionEffect toRemove = player.getActivePotionEffects().stream().filter(potionEffect -> potionEffect.getType().getId() == effect.getType().getId()).findFirst().orElse(null);
        if (toRemove == null) {
            return;
        }
        if (toRemove.getAmplifier() != effect.getAmplifier() && toRemove.getDuration() > effect.getDuration()) {
            return;
        }
        player.removePotionEffect(effect.getType());
    }
}
