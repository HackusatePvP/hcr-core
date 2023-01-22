package dev.hcr.hcf.koths.commands;

import dev.hcr.hcf.koths.commands.staff.KothCreateCommand;
import dev.hcr.hcf.koths.commands.staff.KothSetCenterZoneCommand;

public class KothCommandManager {

    public KothCommandManager() {
        new KothCreateCommand();
        new KothSetCenterZoneCommand();
    }
}
