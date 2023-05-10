package dev.hcr.hcf.commands.players.sotw;

import dev.hcr.hcf.commands.players.sotw.admin.SOTWStartCommand;
import dev.hcr.hcf.commands.players.sotw.admin.SOTWStopCommand;
import dev.hcr.hcf.commands.players.sotw.players.SOTWEnableCommand;
import dev.hcr.hcf.commands.players.sotw.players.SOTWHelpCommand;

public class SOTWCommandManager {

    public SOTWCommandManager() {
        // Admins
        new SOTWStartCommand();
        new SOTWStopCommand();

        // Players
        new SOTWEnableCommand();
        new SOTWHelpCommand();
    }
}
