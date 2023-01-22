package dev.hcr.hcf.pvpclass.types;

import dev.hcr.hcf.pvpclass.PvPClass;
import dev.hcr.hcf.pvpclass.events.ClassUnequippedEvent;
import dev.hcr.hcf.pvpclass.structure.Abilities;
import dev.hcr.hcf.pvpclass.types.bard.objects.Effect;
import dev.hcr.hcf.users.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

public class RogueClass extends PvPClass implements Listener {

    public RogueClass() {
        super("rogue");
    }

    @Override
    public String getDisplayName() {
        return "&bRogue";
    }

    @Override
    public Effect[] getEffects(Player player) {
        return new Effect[] {
                new Effect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2), true),
                new Effect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0), true),
                new Effect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1), true)
        };
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
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        User user = User.getUser(player.getUniqueId());
        if (!hasClassEquipped(player, this)) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getItem() == null) return;
        Material itemType = event.getItem().getType();
        if (itemType != Material.SUGAR && itemType != Material.FEATHER) return;
        ItemStack item = event.getItem();
        Abilities ability;
        switch (itemType) {
            case SUGAR:
                ability = Abilities.ROGUE_SPEED;
                if (user.hasActiveTimer(ability)) {
                    player.sendMessage(ChatColor.RED + "You are still on cooldown for " + user.getActiveTimer(ability).getTimerDisplay());
                    return;
                }
                applyEffect(player, new PotionEffect(PotionEffectType.SPEED, ability.getDuration(), ability.getAmplifier()));
                user.setTimer(ability, true);
                item.setAmount(item.getAmount() - 1);
                break;
            case FEATHER:
                ability = Abilities.ROGUE_JUMP_BOOST;
                if (user.hasActiveTimer(ability)) {
                    player.sendMessage(ChatColor.RED + "You are still on cooldown for " + user.getActiveTimer(ability).getTimerDisplay());
                    return;
                }
                user.setTimer(ability, true);
                applyEffect(player, new PotionEffect(PotionEffectType.JUMP, ability.getDuration(), ability.getAmplifier()));
                item.setAmount(item.getAmount() - 1);
        }
    }
}
