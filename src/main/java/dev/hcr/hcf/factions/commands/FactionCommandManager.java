package dev.hcr.hcf.factions.commands;

import dev.hcr.hcf.factions.commands.captain.FactionInviteCommand;
import dev.hcr.hcf.factions.commands.leader.FactionCreateCommand;
import dev.hcr.hcf.factions.commands.member.FactionChatCommand;
import dev.hcr.hcf.factions.commands.member.FactionHelpCommand;
import dev.hcr.hcf.factions.commands.member.FactionJoinCommand;
import dev.hcr.hcf.factions.commands.member.FactionShowCommand;
import dev.hcr.hcf.factions.commands.staff.FactionTestCommand;

public class FactionCommandManager {

    public FactionCommandManager() {
        // MEMBER COMMANDS
        new FactionChatCommand();
        new FactionJoinCommand();
        new FactionShowCommand();

        // CAPTAIN COMMANDS
        new FactionInviteCommand();

        // LEADER COMMANDS
        new FactionCreateCommand();

        // STAFF COMMANDS
        new FactionTestCommand();

        // Register last for pagination
        new FactionHelpCommand();
    }

}
