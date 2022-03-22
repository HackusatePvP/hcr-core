package dev.hcr.hcf.pvpclass.types;

import dev.hcr.hcf.pvpclass.PvPClass;
import dev.hcr.hcf.pvpclass.events.archer.ArcherTagPlayerEvent;
import dev.hcr.hcf.pvpclass.structure.Abilities;
import dev.hcr.hcf.pvpclass.tasks.objects.Effect;
import dev.hcr.hcf.timers.events.TimerExpireEvent;
import dev.hcr.hcf.timers.events.TimerStartEvent;
import dev.hcr.hcf.timers.events.TimerStopEvent;
import dev.hcr.hcf.timers.types.player.ArcherTagTimer;
import dev.hcr.hcf.users.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class ArcherClass extends PvPClass implements Listener {
    private final Effect[] effects;
    private final Map<Player, Float> arrowForceTracker = new HashMap<>();
    private final Collection<Player> archerTagged = new HashSet<>();

    public ArcherClass() {
        super("archer");
        effects = new Effect[]{
                new Effect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2), true, true),
                new Effect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 2), true, true)
        };
    }

    @Override
    public String getDisplayName() {
        return "&eArcher";
    }

    @Override
    public Effect[] getEffects(Player ignored) {
        return effects;
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
    public void onBowShootEvent(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        float force = event.getForce();
        arrowForceTracker.put(player, force);
    }

    @EventHandler
    public void onArrowDamageByArcher(EntityDamageByEntityEvent event) {
        if (event.getEntity() == event.getDamager() || !(event.getDamager() instanceof Arrow) || !(event.getEntity() instanceof Player)) {
            return;
        }
        Arrow arrow = (Arrow) event.getDamager();
        if (!(arrow.getShooter() instanceof Player)) {
            return;
        }
        Player archer = (Player) arrow.getShooter();
        Player tagged = (Player) event.getEntity();
        if (!hasClassEquipped(archer, this)) {
            return;
        }
        float force = arrowForceTracker.get(archer);
        if (force <= 0.5f) {
            return;
        }
        ArcherTagPlayerEvent tagPlayerEvent = new ArcherTagPlayerEvent(archer, tagged);
        Bukkit.getPluginManager().callEvent(tagPlayerEvent);
    }

    @EventHandler
    public void onArcherTagPlayer(ArcherTagPlayerEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getTagged();
        User user = User.getUser(player.getUniqueId());
        user.setTimer("archer_tag", true);
        archerTagged.add(player);
    }

    @EventHandler
    public void onTimerExpireEvent(TimerExpireEvent event) {
        if (!(event.getTimer() instanceof ArcherTagTimer)) return;
        ArcherTagTimer timer = (ArcherTagTimer) event.getTimer();
        if (timer == null) return;
        Player player = event.getAffected()[0];
        archerTagged.remove(player);
    }

    @EventHandler
    public void onTimerStopEvent(TimerStopEvent event) {
        if (!(event.getTimer() instanceof ArcherTagTimer)) return;
        ArcherTagTimer timer = (ArcherTagTimer) event.getTimer();
        if (timer == null) return;
        Player player = event.getAffected()[0];
        archerTagged.remove(player);
    }


    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        User user = User.getUser(player.getUniqueId());
        if (!hasClassEquipped(player, this)) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getItem() == null) return;
        Material itemType = event.getItem().getType();
        if (itemType != Material.SUGAR && itemType != Material.IRON_INGOT) return;
        ItemStack item = event.getItem();
        Abilities ability;
        switch (itemType) {
            case SUGAR:
                ability = Abilities.ARCHER_SPEED;
                if (user.hasEffectCooldown(ability)) {
                    player.sendMessage(ChatColor.RED + "You are still on cooldown for " + user.getEffectCooldown(ability).getTimerDisplay());
                    return;
                }
                getPlayerEffectTask(player).addEffect(new Effect(new PotionEffect(PotionEffectType.SPEED, 100, 5), false, true));
                user.addEffectCooldown(ability);
                item.setAmount(item.getAmount() - 1);
                break;
            case IRON_INGOT:
                ability = Abilities.ARCHER_RESISTANCE;
                if (user.hasEffectCooldown(ability)) {
                    player.sendMessage(ChatColor.RED + "You are still on cooldown for " + user.getEffectCooldown(ability).getTimerDisplay());
                    return;
                }
                getPlayerEffectTask(player).addEffect(new Effect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 4), false, true));
                user.addEffectCooldown(ability);
                item.setAmount(item.getAmount() - 1);
        }
    }
}
