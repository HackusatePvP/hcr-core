package dev.hcr.hcf.koths.commands;

import dev.hcr.hcf.koths.commands.staff.KothCreateCommand;
import dev.hcr.hcf.koths.commands.staff.KothSetCenterZoneCommand;
import dev.hcr.hcf.koths.commands.staff.KothStartCommand;

public class KothCommandManager {

    public KothCommandManager() {
        new KothStartCommand();
        new KothCreateCommand();
        new KothSetCenterZoneCommand();
    }
}
