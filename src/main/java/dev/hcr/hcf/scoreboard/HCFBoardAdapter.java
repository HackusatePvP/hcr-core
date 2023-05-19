package dev.hcr.hcf.scoreboard;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.pvpclass.types.bard.BardClass;
import dev.hcr.hcf.timers.Timer;
import dev.hcr.hcf.timers.types.server.SOTWTimer;
import dev.hcr.hcf.users.User;
import io.github.thatkawaiisam.assemble.AssembleAdapter;
import io.github.thatkawaiisam.assemble.events.AssembleBoardCreatedEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class HCFBoardAdapter implements AssembleAdapter {

    @Override
    public String getTitle(Player player) {
        // I will not be doing configuration at this time, everything will be hard coded in.
        // Thank god you are seeing this and have source to make the changes you need right?
        return "&4&lHCR &7[Map I]";
    }

    @Override
    public List<String> getLines(Player player) {
        User user = User.getUser(player.getUniqueId());
        List<String> lines = new ArrayList<>();
        lines.add("----------------");
        for (Timer timer : Timer.getTimers()) {
            lines.add("| " + timer.getDisplayName(user) + ": " + timer.getTimerDisplay());
        }
        List<Timer> kitTimers = new ArrayList<>();
        if (user.getActiveTimers() != null) {
            for (Timer timer : user.getActiveTimers()) {
                // This is a little complex but is needed for correct output of formatted string
                if (timer == null) continue;
                if (timer.isEffectTimer()) {
                    kitTimers.add(timer);
                  //lines.add("| * " + timer.getDisplayName() + ": " + timer.getTimerDisplay());
                } else {
                    lines.add("| " + timer.getDisplayName(user) + ": &7" + timer.getTimerDisplay());
                }
            }
        }
        if (user.getCurrentClass() != null) {
            if (user.hasAnyEffectCooldown()) {
                lines.add("| " + user.getCurrentClass().getDisplayName() + ": ");
                if (user.getCurrentClass() instanceof BardClass) {
                    BardClass bardClass = (BardClass) user.getCurrentClass();
                    lines.add("|  &dEnergy: &7" + bardClass.getEnergyTracker(player).getEnergy());
                }
                for (Timer timer : kitTimers) {
                    lines.add("| " + timer.getDisplayName(user) + ": &7" + timer.getTimerDisplay());
                }
            } else {
                lines.add("| " + user.getCurrentClass().getDisplayName() + ": &7(Equipped)");
                if (user.getCurrentClass() instanceof BardClass) {
                    BardClass bardClass = (BardClass) user.getCurrentClass();
                    if (bardClass.getEnergyTracker(player) != null) {
                        lines.add("|  &dEnergy: &7" + bardClass.getEnergyTracker(player).getEnergy());
                    }
                }
            }
        }
        lines.add("| ");
        lines.add("|     &cplay.hcr.dev      ");
        lines.add("----------------");
        if (lines.size() == 4) {
            return new ArrayList<>();
        }
        return lines;
    }

}
