package dev.hcr.hcf.users.faction;

public enum ChatChannel {
    TOGGLED,
    PUBLIC,
    FACTION,
    CAPTAIN,
    ALLY;

    public static ChatChannel getChannel(String channel) {
        switch (channel.toLowerCase()) {
            case "toggle":
            case "t":
                return TOGGLED;
            case "faction":
            case "f":
                return FACTION;
            case "captain:":
            case "c":
                return CAPTAIN;
            case "ally":
            case "a":
                return ALLY;
            case "public":
            case "p":
            default:
                return PUBLIC;
        }
    }
}
