package dev.hcr.hcf.factions.commands;

import dev.hcr.hcf.factions.commands.captain.FactionInviteCommand;
import dev.hcr.hcf.factions.commands.captain.FactionKickCommand;
import dev.hcr.hcf.factions.commands.captain.FactionSetHomeCommand;
import dev.hcr.hcf.factions.commands.captain.FactionWithdrawCommand;
import dev.hcr.hcf.factions.commands.coleader.FactionClaimCommand;
import dev.hcr.hcf.factions.commands.coleader.FactionDemoteCommand;
import dev.hcr.hcf.factions.commands.coleader.FactionPromoteCommand;
import dev.hcr.hcf.factions.commands.coleader.FactionUnclaimCommand;
import dev.hcr.hcf.factions.commands.leader.FactionCreateCommand;
import dev.hcr.hcf.factions.commands.leader.FactionDisbandCommand;
import dev.hcr.hcf.factions.commands.leader.FactionLeaderCommand;
import dev.hcr.hcf.factions.commands.member.*;
import dev.hcr.hcf.factions.commands.staff.*;

public class FactionCommandManager {

    public FactionCommandManager() {
        // MEMBER COMMANDS
        new FactionChatCommand();
        new FactionDepositCommand();
        new FactionHomeCommand();
        new FactionJoinCommand();
        new FactionLeaveCommand();
        new FactionListCommand();
        new FactionMapCommand();
        new FactionShowCommand();

        // CAPTAIN COMMANDS
        new FactionInviteCommand();
        new FactionWithdrawCommand();
        new FactionKickCommand();
        new FactionSetHomeCommand();

        // COLEADER COMMANDS
        new FactionClaimCommand();
        new FactionDemoteCommand();
        new FactionPromoteCommand();
        new FactionUnclaimCommand();

        // LEADER COMMANDS
        new FactionCreateCommand();
        new FactionDisbandCommand();
        new FactionLeaderCommand();

        // STAFF COMMANDS
        new FactionBypassCommand();
        new FactionClaimForCommand();
        new FactionForceJoinCommand();
        new FactionForceKickCommand();
        new FactionForceLeaderCommand();
        new FactionResetClaimsCommand();
        new FactionForceSetHomeCommand();
        new FactionTestCommand();

        // Register last for pagination
        new FactionHelpCommand();
    }

}
