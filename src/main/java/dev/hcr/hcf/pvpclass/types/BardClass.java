package dev.hcr.hcf.pvpclass.types;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.pvpclass.PvPClass;
import dev.hcr.hcf.pvpclass.structure.Abilities;
import dev.hcr.hcf.pvpclass.tasks.EffectApplyTask;
import dev.hcr.hcf.pvpclass.tasks.EnergyBuildTask;
import dev.hcr.hcf.pvpclass.tasks.objects.Effect;
import dev.hcr.hcf.users.User;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
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
        return new ItemStack[] {
                new ItemStack(Material.SUGAR),
                new ItemStack(Material.IRON_INGOT),
                new ItemStack(Material.BLAZE_POWDER),
                new ItemStack(Material.FEATHER),
                new ItemStack(Material.GHAST_TEAR),
                new ItemStack(Material.SPIDER_EYE)
        };
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        User user = User.getUser(player.getUniqueId());
        if (user.getCurrentClass() == this) {
            unequip(player);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        User user = User.getUser(player.getUniqueId());
        if (!hasClassEquipped(player, this)) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getItem() == null) return;
        ItemStack item = event.getItem();
        Material itemType = event.getItem().getType();
        if (Arrays.stream(getClickableEffects()).noneMatch(itemStack -> itemStack.getType() == itemType)) return;
        Abilities ability;

        switch (itemType) {
            case SUGAR:
                System.out.println("Sugar");
                ability = Abilities.BARD_SPEED;
                handleFriendlyBardEffect(user, new Effect(new PotionEffect(PotionEffectType.SPEED, 20 * 5, 2), false, true));
                user.addEffectCooldown(ability);
                item.setAmount(item.getAmount() - 1);
                break;
            case IRON_INGOT:
                ability = Abilities.BARD_RESISTANCE;
                handleFriendlyBardEffect(user, new Effect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 3), false, true));
                user.addEffectCooldown(ability);
                item.setAmount(item.getAmount() - 1);
                break;
            case BLAZE_POWDER:
                ability = Abilities.BARD_STRENGTH;
                handleFriendlyBardEffect(user, new Effect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 5, 1), false, true));
                user.addEffectCooldown(ability);
                item.setAmount(item.getAmount() - 1);
                break;
            case FEATHER:
                ability = Abilities.BARD_JUMP_BOOST;
                handleFriendlyBardEffect(user, new Effect(new PotionEffect(PotionEffectType.JUMP, 20 * 10, 5), false, true));
                user.addEffectCooldown(ability);
                item.setAmount(item.getAmount() - 1);
                break;
            case GHAST_TEAR:
                ability = Abilities.BARD_REGENERATION;
                handleFriendlyBardEffect(user, new Effect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 2), false, true));
                user.addEffectCooldown(ability);
                item.setAmount(item.getAmount() - 1);
                break;
            case SPIDER_EYE:
                // TODO: 2/27/2022
                break;
        }

    }

    private void handleFriendlyBardEffect(User bard, Effect effect) {
        System.out.println("Handling effects given by " + bard.getName());
        Player bardPlayer = bard.toPlayer();
        if (effect.isSelfApply()) {
            System.out.println("Effect can be applied to bard");
            getPlayerEffectTask(bardPlayer).addEffect(effect);
        }
        if (bard.getFaction() != null) {
            System.out.println("Bard has faction.");
            PlayerFaction playerFaction = (PlayerFaction) bard.getFaction();
            System.out.println("Scanning nearby players");
            for (Player factionMembers : Faction.getNearbyPlayers(bardPlayer, 15)) {
                System.out.println("Found " + factionMembers.getName());
                if (playerFaction.hasMember(factionMembers.getUniqueId())) {
                    System.out.println(factionMembers.getName() + " is in faction " + playerFaction.getName());
                    System.out.println("Adding effect to player");
                    EffectApplyTask task = getPlayerEffectTask(factionMembers);
                    if (task == null) {
                        System.out.println("Effect task for player is null creating...");
                        createEffectTask(factionMembers, effect);
                    }
                }
            }
        }
    }

    private void handleEnemyBardEffect(Player player, Effect effect) {

    }
}
