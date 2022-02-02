package dev.hcr.hcf.factions.commands;

import dev.hcr.hcf.factions.commands.captain.FactionInviteCommand;
import dev.hcr.hcf.factions.commands.captain.FactionWithdrawCommand;
import dev.hcr.hcf.factions.commands.coleader.FactionClaimCommand;
import dev.hcr.hcf.factions.commands.leader.FactionCreateCommand;
import dev.hcr.hcf.factions.commands.member.*;
import dev.hcr.hcf.factions.commands.staff.FactionTestCommand;

public class FactionCommandManager {

    public FactionCommandManager() {
        // MEMBER COMMANDS
        new FactionChatCommand();
        new FactionDepositCommand();
        new FactionJoinCommand();
        new FactionShowCommand();

        // CAPTAIN COMMANDS
        new FactionInviteCommand();
        new FactionWithdrawCommand();

        // COLEADER COMMANDS
        new FactionClaimCommand();

        // LEADER COMMANDS
        new FactionCreateCommand();

        // STAFF COMMANDS
        new FactionTestCommand();

        // Register last for pagination
        new FactionHelpCommand();
    }

}
