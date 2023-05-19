package dev.hcr.hcf.scoreboard;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.pvpclass.events.archer.ArcherTagExpireEvent;
import dev.hcr.hcf.pvpclass.events.archer.ArcherTagPlayerEvent;
import dev.hcr.hcf.scoreboard.events.PlayerInvisibilityEvent;
import dev.hcr.hcf.scoreboard.events.PlayerVisibilityEvent;
import dev.hcr.hcf.scoreboard.events.UserTeamLoadEvent;
import dev.hcr.hcf.users.User;
import io.github.thatkawaiisam.assemble.events.AssembleBoardCreatedEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardListener implements Listener {

    @EventHandler
    public void onArcherTagExpireEvent(ArcherTagExpireEvent event) {
        Player player = event.getTagged();
        User user = User.getUser(player.getUniqueId());
        boolean inFaction = user.hasFaction();
        PlayerFaction faction = (PlayerFaction) user.getFaction();
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.getName().equalsIgnoreCase(player.getName())) continue;
            Scoreboard scoreboard = online.getScoreboard();
            Team enemy = (scoreboard.getTeam("enemy") == null ? scoreboard.registerNewTeam("enemy") : scoreboard.getTeam("enemy"));
            Team invis = (scoreboard.getTeam("enemy") == null ? scoreboard.registerNewTeam("enemy") : scoreboard.getTeam("enemy"));
            Team archertag = (scoreboard.getTeam("archertag") == null ? scoreboard.registerNewTeam("archertag") : scoreboard.getTeam("archertag"));
            if (inFaction) {
                System.out.println(player.getName() + " has faction");
                if (!faction.hasMember(online.getUniqueId())) {
                    System.out.println(online.getName() + " is not in the same faction as " + player.getName());
                    archertag.removePlayer(player);
                    if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                        System.out.println(player.getName() + " has invisibility");
                        enemy.removePlayer(player);
                        invis.addPlayer(player);
                    } else {
                        System.out.println(player.getName() + " does not have invisibility");
                        enemy.addPlayer(player);
                    }
                }
            } else {
                System.out.println(player.getName() + " does not have a faction");
                archertag.removePlayer(player);
                if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    invis.addPlayer(player);
                    enemy.removePlayer(player);
                } else {
                    enemy.addPlayer(player);
                }
            }
        }
    }

    @EventHandler
    public void onArcherTagEvent(ArcherTagPlayerEvent event) {
        Player player = event.getTagged();
        User user = User.getUser(player.getUniqueId());
        boolean inFaction = user.hasFaction();
        for (Player online : Bukkit.getOnlinePlayers()) {
            Scoreboard scoreboard = online.getScoreboard();
            Team enemy = (scoreboard.getTeam("enemy") == null ? scoreboard.registerNewTeam("enemy") : scoreboard.getTeam("enemy"));
            Team invis = (scoreboard.getTeam("enemy") == null ? scoreboard.registerNewTeam("enemy") : scoreboard.getTeam("enemy"));
            Team archertag = (scoreboard.getTeam("archertag") == null ? scoreboard.registerNewTeam("archertag") : scoreboard.getTeam("archertag"));
            if (inFaction) {
                PlayerFaction faction = (PlayerFaction) user.getFaction();
                if (!faction.hasMember(online.getUniqueId())) {
                    archertag.addPlayer(player);
                    enemy.removePlayer(player);
                }
            } else {
                archertag.addPlayer(player);
                enemy.removePlayer(player);
                invis.removePlayer(player);
            }
        }
    }

    @EventHandler
    public void onPlayerInvisibilityEvent(PlayerInvisibilityEvent event) {
        System.out.println("Calling invisibility Event");
        Player player = event.getPlayer();
        User user = User.getUser(player.getUniqueId());
        boolean inFaction = user.hasFaction();
        for (Player online : Bukkit.getOnlinePlayers()) {
            System.out.println("Setting invisibility teams for " + online.getName());
            Scoreboard scoreboard = online.getScoreboard();
            Team enemy = (scoreboard.getTeam("enemy") == null ? scoreboard.registerNewTeam("enemy") : scoreboard.getTeam("enemy"));
            Team invis = (scoreboard.getTeam("invis") == null ? scoreboard.registerNewTeam("invis") : scoreboard.getTeam("invis"));
            if (inFaction) {
                System.out.println(player.getName() + " has faction");
                PlayerFaction faction = (PlayerFaction) user.getFaction();
                if (!faction.hasMember(online.getUniqueId())) {
                    System.out.println(online.getName() + " is not in the same faction as " + player.getName());
                    invis.addPlayer(player);
                    enemy.removePlayer(player);
                }
            } else {
                System.out.println(player.getName() + " is not in a faction.");
                invis.addPlayer(player);
                enemy.removePlayer(player);
            }
        }
    }

    @EventHandler
    public void onPlayerVisibilityEvent(PlayerVisibilityEvent event) {
        System.out.println("Calling visibility event");
        Player player = event.getPlayer();
        User user = User.getUser(player.getUniqueId());
        boolean inFaction = user.hasFaction();
        for (Player online : Bukkit.getOnlinePlayers()) {
            Scoreboard scoreboard = online.getScoreboard();
            Team enemy = (scoreboard.getTeam("enemy") == null ? scoreboard.registerNewTeam("enemy") : scoreboard.getTeam("enemy"));
            Team invis = (scoreboard.getTeam("invis") == null ? scoreboard.registerNewTeam("invis") : scoreboard.getTeam("invis"));
            if (inFaction) {
                PlayerFaction faction = (PlayerFaction) user.getFaction();
                if (!faction.hasMember(online.getUniqueId())) {
                    invis.removePlayer(player);
                    enemy.addPlayer(player);
                }
            } else {
                invis.removePlayer(player);
                enemy.addPlayer(player);
            }
        }
    }

    @EventHandler
    public void onBoardCreate(AssembleBoardCreatedEvent event) {
        User user = User.getUser(event.getBoard().getUuid());
        Scoreboard scoreboard = event.getBoard().getScoreboard();
        //HCF.getPlugin().getTeamManager().registerTeams(scoreboard);
        UserTeamLoadEvent loadEvent = new UserTeamLoadEvent(user, scoreboard);
        Bukkit.getPluginManager().callEvent(loadEvent);

        HCF.getPlugin().getTeamManager().loadPlayer(user.toPlayer());
    }

    @EventHandler
    public void onUserTeamLoadEvent(UserTeamLoadEvent event) {
        Scoreboard scoreboard = event.getScoreboard();
        Team friendly = (scoreboard.getTeam("friendly") == null ? scoreboard.registerNewTeam("friendly") : scoreboard.getTeam("friendly"));
        Team enemy = (scoreboard.getTeam("enemy") == null ? scoreboard.registerNewTeam("enemy") : scoreboard.getTeam("enemy"));
        Team focus = (scoreboard.getTeam("focus") == null ? scoreboard.registerNewTeam("focus") : scoreboard.getTeam("focus"));
        Team ally = (scoreboard.getTeam("ally") == null ? scoreboard.registerNewTeam("ally") : scoreboard.getTeam("ally"));
        Team invis = (scoreboard.getTeam("invis") == null ? scoreboard.registerNewTeam("invis") : scoreboard.getTeam("invis"));
        Team archertag = (scoreboard.getTeam("archertag") == null ? scoreboard.registerNewTeam("archertag") : scoreboard.getTeam("archertag"));

        friendly.setPrefix("§a");
        enemy.setPrefix("§c");
        ally.setPrefix("§9");
        focus.setPrefix("§d");
        archertag.setPrefix("§e");

        friendly.setNameTagVisibility(NameTagVisibility.ALWAYS);
        enemy.setNameTagVisibility(NameTagVisibility.ALWAYS);
        focus.setNameTagVisibility(NameTagVisibility.ALWAYS);
        ally.setNameTagVisibility(NameTagVisibility.ALWAYS);
        archertag.setNameTagVisibility(NameTagVisibility.ALWAYS);
        // Apply invis fix
        invis.setNameTagVisibility(NameTagVisibility.NEVER);
        // Allow teammates to see invis
        friendly.setCanSeeFriendlyInvisibles(true);
    }
}
