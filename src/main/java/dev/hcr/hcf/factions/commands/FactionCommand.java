package dev.hcr.hcf.factions.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public abstract class FactionCommand {
    private String permission;
    private final static Map<String, FactionCommand> commandMap = new HashMap<>();

    public FactionCommand(String name) {
        commandMap.put(name.toLowerCase(), this);
    }

    public FactionCommand(String name, String permission) {
        commandMap.put(name.toLowerCase(), this);
        this.permission = "hcf.faction.commands." + permission;
    }

    public String getPermission() {
        return "hcf.faction.commands." + permission;
    }

    public static FactionCommand getCommand(String name) {
        return commandMap.get(name.toLowerCase());
    }

    public abstract void execute(CommandSender sender, Command command, String label, String[] args);
}
