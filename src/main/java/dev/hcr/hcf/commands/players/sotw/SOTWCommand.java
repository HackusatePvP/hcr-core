package dev.hcr.hcf.commands.players.sotw;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SOTWCommand {
    private final String name;
    private String permission;
    private final String description;
    private String[] aliases;
    private final static Map<String, SOTWCommand> commandMap = new HashMap<>();
    private final static Map<String, SOTWCommand> aliasMap = new HashMap<>();

    public SOTWCommand(String name, String description) {
        this.name = name;
        this.description = description;
        commandMap.put(name.toLowerCase(), this);
    }

    public SOTWCommand(String name, String permission, String description) {
        this.name = name;
        this.description = description;
        this.permission = "hcf.faction.commands." + permission;
        commandMap.put(name.toLowerCase(), this);
    }

    public SOTWCommand(String name, String description, String... aliases) {
        this.name = name;
        this.description = description;
        commandMap.put(name.toLowerCase(), this);
        this.aliases = aliases;
        for (String s : aliases) {
            aliasMap.put(s.toLowerCase(), this);
        }
    }

    public SOTWCommand(String name, String description, String permission, String... aliases) {
        this.name = name;
        this.description = description;
        this.permission = permission;
        this.aliases = aliases;
        commandMap.put(name.toLowerCase(), this);
        for (String s : aliases) {
            aliasMap.put(s.toLowerCase(), this);
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPermission() {
        return permission;
    }

    public String[] getAliases() {
        return aliases;
    }

    public static SOTWCommand getCommand(String name) {
        if (commandMap.get(name) == null) {
            return aliasMap.get(name);
        }
        return commandMap.get(name);
    }

    public static List<String> getRegisteredCommands() {
        List<String> toReturn = new ArrayList<>();
        commandMap.values().forEach(factionCommand -> toReturn.add(factionCommand.getName()));
        return toReturn;
    }

    public abstract void execute(CommandSender sender, Command command, String label, String[] args);

    public abstract List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args);
}
