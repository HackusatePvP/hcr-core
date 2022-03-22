package dev.hcr.hcf.scoreboard;

import dev.hcr.hcf.timers.Timer;
import dev.hcr.hcf.users.User;
import io.github.thatkawaiisam.assemble.AssembleAdapter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HCFBoardAdapter implements AssembleAdapter {

    @Override
    public String getTitle(Player player) {
        // I will ne bot doing configuration at this time, everything will be hard coded in.
        // Thank god you are seeing this and have source to make the changes you need right?
        return "&4&lHCR &7[Map I]";
    }

    @Override
    public List<String> getLines(Player player) {
        List<String> lines = new ArrayList<>();
        lines.add("----------------");
        User user = User.getUser(player.getUniqueId());
        List<Timer> kitTimers = new ArrayList<>();
        if (user.getActiveTimers() != null) {
            for (Timer timer : user.getActiveTimers()) {
                // This is a little complex but is needed for correct output of formatted string
                if (timer == null) continue;
                if (timer.isEffectTimer()) {
                    kitTimers.add(timer);
                  //lines.add("| * " + timer.getDisplayName() + ": " + timer.getTimerDisplay());
                } else {
                    lines.add("| " + timer.getDisplayName() + ": " + timer.getTimerDisplay());
                }
            }
        }
        if (user.getCurrentClass() != null) {
            if (user.hasAnyEffectCooldown()) {
                lines.add(user.getCurrentClass().getDisplayName() + ": ");
                for (Timer timer : kitTimers) {
                    lines.add("| " + timer.getDisplayName() + ": " + timer.getTimerDisplay());
                }
            } else {
                lines.add("| " + user.getCurrentClass().getDisplayName() + ": &7(Equipped)");
            }
        }
        for (Timer timer : Timer.getTimers()) {
            lines.add(timer.getDisplayName() + ": " + timer.getTimerDisplay());
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
