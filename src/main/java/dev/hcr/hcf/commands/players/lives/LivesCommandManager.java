package dev.hcr.hcf.commands.players.lives;

public class LivesCommandManager {

    public LivesCommandManager() {
        new LivesCheckCommand();
        new LivesHelpCommand();
        new LivesSendCommand();
        new LivesReviveCommand();
    }
}
