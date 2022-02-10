package dev.hcr.hcf.factions.commands;

import dev.hcr.hcf.factions.commands.captain.FactionInviteCommand;
import dev.hcr.hcf.factions.commands.captain.FactionKickCommand;
import dev.hcr.hcf.factions.commands.captain.FactionWithdrawCommand;
import dev.hcr.hcf.factions.commands.coleader.FactionClaimCommand;
import dev.hcr.hcf.factions.commands.coleader.FactionPromoteCommand;
import dev.hcr.hcf.factions.commands.leader.FactionCreateCommand;
import dev.hcr.hcf.factions.commands.leader.FactionDisbandCommand;
import dev.hcr.hcf.factions.commands.member.*;
import dev.hcr.hcf.factions.commands.staff.FactionTestCommand;

public class FactionCommandManager {

    public FactionCommandManager() {
        // MEMBER COMMANDS
        new FactionChatCommand();
        new FactionDepositCommand();
        new FactionJoinCommand();
        new FactionLeaveCommand();
        new FactionMapCommand();
        new FactionShowCommand();

        // CAPTAIN COMMANDS
        new FactionInviteCommand();
        new FactionWithdrawCommand();
        new FactionKickCommand();

        // COLEADER COMMANDS
        new FactionClaimCommand();
        new FactionPromoteCommand();

        // LEADER COMMANDS
        new FactionCreateCommand();
        new FactionDisbandCommand();

        // STAFF COMMANDS
        new FactionTestCommand();

        // Register last for pagination
        new FactionHelpCommand();
    }

}
