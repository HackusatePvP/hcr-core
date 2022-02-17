package dev.hcr.hcf.factions.commands;

import dev.hcr.hcf.factions.commands.captain.FactionInviteCommand;
import dev.hcr.hcf.factions.commands.captain.FactionKickCommand;
import dev.hcr.hcf.factions.commands.captain.FactionSetHomeCommand;
import dev.hcr.hcf.factions.commands.captain.FactionWithdrawCommand;
import dev.hcr.hcf.factions.commands.coleader.FactionClaimCommand;
import dev.hcr.hcf.factions.commands.coleader.FactionPromoteCommand;
import dev.hcr.hcf.factions.commands.leader.FactionCreateCommand;
import dev.hcr.hcf.factions.commands.leader.FactionDisbandCommand;
import dev.hcr.hcf.factions.commands.member.*;
import dev.hcr.hcf.factions.commands.staff.FactionBypassCommand;
import dev.hcr.hcf.factions.commands.staff.FactionClaimForCommand;
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
        new FactionSetHomeCommand();

        // COLEADER COMMANDS
        new FactionClaimCommand();
        new FactionPromoteCommand();

        // LEADER COMMANDS
        new FactionCreateCommand();
        new FactionDisbandCommand();

        // STAFF COMMANDS
        new FactionBypassCommand();
        new FactionClaimForCommand();
        new FactionTestCommand();

        // Register last for pagination
        new FactionHelpCommand();
    }

}
