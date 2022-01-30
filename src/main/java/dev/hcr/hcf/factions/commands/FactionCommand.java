package dev.hcr.hcf.factions.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.*;

public abstract class FactionCommand {
    private final String name;
    private String permission;
    private final String description;
    private String[] aliases;
    private final static Map<String, FactionCommand> commandMap = new HashMap<>();

    public FactionCommand(String name, String description) {
        this.name = name;
        this.description = description;
        commandMap.put(name.toLowerCase(), this);
    }

    public FactionCommand(String name, String permission, String description) {
        this.name = name;
        this.description = description;
        this.permission = "hcf.faction.commands." + permission;
        commandMap.put(name.toLowerCase(), this);
    }

    public FactionCommand(String name, String description, String... aliases) {
        this.name = name;
        this.description = description;
        commandMap.put(name.toLowerCase(), this);
        for (String s : aliases) {
            commandMap.put(s.toLowerCase(), this);
        }
    }

    public FactionCommand(String name, String description, String permission, String... aliases) {
        this.name = name;
        this.description = description;
        this.permission = permission;
        commandMap.put(name.toLowerCase(), this);
        for (String s : aliases) {
            commandMap.put(s.toLowerCase(), this);
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPermission() {
        return "hcf.faction.commands." + permission;
    }

    public static FactionCommand getCommand(String name) {
        return commandMap.get(name.toLowerCase());
    }

    public static List<String> getRegisteredCommands() {
        List<String> toReturn = new ArrayList<>();
        commandMap.values().forEach(factionCommand -> toReturn.add(factionCommand.getName()));
        return toReturn;
    }

    public abstract void execute(CommandSender sender, Command command, String label, String[] args);

    public abstract List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args);
}
