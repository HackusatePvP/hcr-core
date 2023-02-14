package dev.hcr.hcf.scoreboard;

import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.backend.types.PropertiesConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Collection;

public class TeamManager {
    private final boolean debug = PropertiesConfiguration.getPropertiesConfiguration("hcf.properties").getBoolean("debug");

    public void registerTeams(Scoreboard scoreboard) {
        Team friendly = (scoreboard.getTeam("friendly") == null ? scoreboard.registerNewTeam("friendly") : scoreboard.getTeam("friendly"));
        Team enemy = (scoreboard.getTeam("enemy") == null ? scoreboard.registerNewTeam("enemy") : scoreboard.getTeam("enemy"));
        Team focus = (scoreboard.getTeam("focus") == null ? scoreboard.registerNewTeam("focus") : scoreboard.getTeam("focus"));
        Team ally = (scoreboard.getTeam("ally") == null ? scoreboard.registerNewTeam("ally") : scoreboard.getTeam("ally"));
        Team invis = (scoreboard.getTeam("invis") == null ? scoreboard.registerNewTeam("invis") : scoreboard.getTeam("invis"));
        Team archertag = (scoreboard.getTeam("archertag") == null ? scoreboard.registerNewTeam("archertag") : scoreboard.getTeam("archertag"));
        applyPrefix(friendly, enemy, ally, invis, focus, archertag);
    }

    public void loadPlayer(Player player) {
        User user = User.getUser(player.getUniqueId());

        // Setup teams for players scoreboard
        if (debug) {
            System.out.println("Creating and registering teams for " + player.getName());
        }
        Scoreboard scoreboard = player.getScoreboard();
        Team friendly = scoreboard.getTeam("friendly");
        Team enemy = scoreboard.getTeam("enemy");
        Team ally = scoreboard.getTeam("ally");
        Team invis = scoreboard.getTeam("invis");
        Team focus = scoreboard.getTeam("focus");
        Team archertag = scoreboard.getTeam("archertag");

        // Check to see if player has friendlies
        if (debug) {
            System.out.println("Adding " + player.getName() + " to friendlies");
        }
        friendly.addPlayer(player);
        boolean inFaction = user.hasFaction();
        if (debug) {
            System.out.println("Creating relations for online players...");
        }
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.getUniqueId() == player.getUniqueId()) {
                System.out.println(online.getName() + " same player as " + player.getName() + " skipping...");
                continue;
            }
            if (debug) {
                System.out.println("Updating " + online.getName() + "...");
            }
            // Update teams for player
            if (inFaction) {
                if (debug) {
                    System.out.println(player.getName() + " is in a faction.");
                }
                PlayerFaction faction = (PlayerFaction) user.getFaction();
                if (faction.hasMember(online.getUniqueId()) && online.getUniqueId() != player.getUniqueId()) {
                    if (debug) {
                        System.out.println(online.getName() + " is in the faction and is not " + player.getName());
                        System.out.println("Adding " + online.getName() + " to friendlies for " + player.getName());
                    }
                    friendly.addPlayer(online);
                    if (enemy.hasPlayer(online)) {
                        enemy.removePlayer(online);
                    }
                } else {
                    if (debug) {
                        System.out.println("Adding " + online.getName() + " to enemies for " + player.getName());
                    }
                    enemy.addPlayer(online);
                    if (friendly.hasPlayer(online)) {
                        friendly.removePlayer(online);
                    }
                }
                // TODO: 12/16/2022 scan for allies and any focused player
            } else {
                if (debug) {
                    System.out.println(player.getName() + " is not in a faction.");
                }
                if (online.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    if (debug) {
                        System.out.println(online.getName() + " is invisible adding to invis team.");
                    }
                    invis.addPlayer(online);
                }
                if (debug) {
                    System.out.println("Adding " + online.getName() + " to enemies.");
                }
                enemy.addPlayer(online);
                if (friendly.hasPlayer(online)) {
                    friendly.removePlayer(online);
                }
            }

            // Update teams for all other online players and redefine vars
            if (debug) {
                System.out.println("Updating online players relation for " + player.getName());
                System.out.println("Redefining local vars...");
            }
            scoreboard = online.getScoreboard();
            friendly = scoreboard.getTeam("friendly");
            enemy = scoreboard.getTeam("enemy");
            ally = scoreboard.getTeam("ally");
            invis = scoreboard.getTeam("invis");
            focus = scoreboard.getTeam("focus");
            archertag = scoreboard.getTeam("archertag");

            if (friendly.getPrefix().isEmpty()) {
                applyPrefix(friendly, enemy, ally, invis, focus, archertag);
            }

            user = User.getUser(online.getUniqueId());
            inFaction = user.hasFaction();
            if (inFaction) {
                if (debug) {
                    System.out.println(online.getName() + " is in a faction.");
                }
                PlayerFaction faction = (PlayerFaction) user.getFaction();
                // Online player is in a faction see if the player is in the faction.
                if (faction.hasMember(player.getUniqueId())) {
                    if (debug) {
                        System.out.println(player.getName() + " is in the same faction as " + online.getName());
                        System.out.println("Adding " + player.getName() + " to friendlies for " + online.getName());
                    }
                    friendly.addPlayer(player);
                    if (enemy.hasPlayer(player)) {
                        enemy.removePlayer(player);
                    }
                } else {
                    if (debug) {
                        System.out.println("Adding " + player.getName() + " to enemies for " + online.getName());
                    }
                    enemy.addPlayer(player);
                    if (friendly.hasPlayer(player)) {
                        friendly.removePlayer(player);
                    }
                }

                // TODO: 12/16/2022 Check to see if the player is an ally
            } else {
                if (debug) {
                    System.out.println(online.getName() + " is not in a faction.");
                }
                // Check to see if the player is invis
                if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    if (debug) {
                        System.out.println(player.getName() + " is invisible adding to invis team for " + online.getName());
                    }
                    invis.addPlayer(player);
                }
                if (debug) {
                    System.out.println("Adding " + player.getName() + " to enemies for " + online.getName());
                }
                enemy.addPlayer(player);
                if (friendly.hasPlayer(player)) {
                    friendly.removePlayer(player);
                }
            }
        }

        scoreboard = player.getScoreboard();
        friendly = (scoreboard.getTeam("friendly") == null ? scoreboard.registerNewTeam("friendly") : scoreboard.getTeam("friendly"));
        enemy = (scoreboard.getTeam("enemy") == null ? scoreboard.registerNewTeam("enemy") : scoreboard.getTeam("enemy"));
        focus = (scoreboard.getTeam("focus") == null ? scoreboard.registerNewTeam("focus") : scoreboard.getTeam("focus"));
        ally = (scoreboard.getTeam("ally") == null ? scoreboard.registerNewTeam("ally") : scoreboard.getTeam("ally"));
        invis = (scoreboard.getTeam("invis") == null ? scoreboard.registerNewTeam("invis") : scoreboard.getTeam("invis"));
        archertag = (scoreboard.getTeam("archertag") == null ? scoreboard.registerNewTeam("archertag") : scoreboard.getTeam("archertag"));

        applyPrefix(friendly, enemy, ally, invis, focus, archertag);
        if (debug) {
            System.out.println("Validation: ");
            System.out.println("Friendlies: " + friendly.getEntries());
            System.out.println("Enemies: " + enemy.getEntries());
            System.out.println("Allies: " + ally.getEntries());
            System.out.println("Invis: " + invis.getEntries());
            System.out.println("Focus: " + focus.getEntries());
            System.out.println("Archer Tags: " + archertag.getEntries());
        }
        if (friendly.getPrefix().isEmpty()) {
            applyPrefix(friendly, enemy, ally, invis, focus, archertag);
        }
    }

    private void applyPrefix(Team friendly, Team enemy, Team ally, Team invis, Team focus, Team archertag) {
        friendly.setPrefix("§a");
        if (debug) {
            System.out.println("Prefix: " + friendly.getPrefix());
        }
        enemy.setPrefix("§c");
        ally.setPrefix("§9");
        focus.setPrefix("§d");
        archertag.setPrefix("§e");

        // Apply invis fix
        invis.setNameTagVisibility(NameTagVisibility.NEVER);
        // Allow teammates to see invis
        friendly.setCanSeeFriendlyInvisibles(true);
    }

    public void setArcherTag(Player player, boolean add) {
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
                    if (add) {
                        archertag.addPlayer(player);
                        enemy.removePlayer(player);
                    } else {
                        archertag.removePlayer(player);
                        enemy.addPlayer(player);
                    }
                }
            } else {
                if (add) {
                    archertag.addPlayer(player);
                    enemy.removePlayer(player);
                    invis.removePlayer(player);
                } else {
                    archertag.removePlayer(player);
                    enemy.addPlayer(player);
                    if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                        invis.addPlayer(player);
                    }
                }
            }
        }
    }

    public void setInvisibility(Player player, boolean add) {
        User user = User.getUser(player.getUniqueId());
        boolean inFaction = user.hasFaction();
        for (Player online : Bukkit.getOnlinePlayers()) {
            Scoreboard scoreboard = online.getScoreboard();
            Team enemy = (scoreboard.getTeam("enemy") == null ? scoreboard.registerNewTeam("enemy") : scoreboard.getTeam("enemy"));
            Team invis = (scoreboard.getTeam("invis") == null ? scoreboard.registerNewTeam("invis") : scoreboard.getTeam("invis"));
            if (inFaction) {
                PlayerFaction faction = (PlayerFaction) user.getFaction();
                if (!faction.hasMember(player.getUniqueId())) {
                    if (add) {
                        invis.addPlayer(player);
                        enemy.removePlayer(player);
                    } else {
                        invis.removePlayer(player);
                        enemy.addPlayer(player);
                    }
                }
            } else {
                if (add) {
                    invis.addPlayer(player);
                    enemy.removePlayer(player);
                } else {
                    invis.removePlayer(player);
                    enemy.addPlayer(player);
                }
            }
        }
    }

    public void setFocus(PlayerFaction faction, Player target, boolean add) {
        for (Player online : faction.getOnlineMembers()) {
            Scoreboard scoreboard = online.getScoreboard();
            Team focus = (scoreboard.getTeam("focus") == null ? scoreboard.registerNewTeam("focus") : scoreboard.getTeam("focus"));
            if (add) {
                focus.addPlayer(target);
            } else {
                focus.removePlayer(target);
            }
        }
    }

    public void setAlly(PlayerFaction faction, PlayerFaction ally, boolean add) {
        if (add) {
            for (Player member : faction.getOnlineMembers()) {
                Scoreboard scoreboard = member.getScoreboard();
                Team team = (scoreboard.getTeam("ally") == null ? scoreboard.registerNewTeam("ally") : scoreboard.getTeam("ally"));
                for (Player otherFaction : ally.getOnlineMembers()) {
                    team.addPlayer(otherFaction);
                    Scoreboard otherScoreboard = otherFaction.getScoreboard();
                    Team otherTeam = (otherScoreboard.getTeam("ally") == null ? otherScoreboard.registerNewTeam("ally") : otherScoreboard.getTeam("ally"));
                    otherTeam.addPlayer(member);
                }
            }
        } else {
            Collection<Player> allMembers = faction.getOnlineMembers();
            allMembers.addAll(ally.getOnlineMembers());
            for (Player online : allMembers) {
                Scoreboard scoreboard = online.getScoreboard();
                Team team = (scoreboard.getTeam("ally") == null ? scoreboard.registerNewTeam("ally") : scoreboard.getTeam("ally"));
                team.getPlayers().clear();
            }

            // TODO: 12/16/2022 Re-validate players

        }
    }
}
