package dev.hcr.hcf.koths.commands;

import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class KothCommand {
    private final String name;
    private String permission;
    private final String description;
    private String[] aliases;
    private static final Map<String, KothCommand> commandMap = new HashMap<>();
    private static final Map<String, KothCommand> aliasMap = new HashMap<>();

    public KothCommand(String name, String description) {
        this.name = name;
        this.description = description;
        commandMap.put(name, this);
    }

    public KothCommand(String name, String description, String permission) {
        this.name = name;
        this.description = description;
        this.permission = permission;
        commandMap.put(name, this);
    }

    public KothCommand(String name, String description, String permission, String... aliases) {
        this.name = name;
        this.description = description;
        this.permission = permission;
        this.aliases = aliases;
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

    public static KothCommand getCommand(String name) {
        if (aliasMap.containsKey(name)) {
            return aliasMap.get(name);
        } else {
            return commandMap.get(name);
        }
    }

    public static Collection<KothCommand> getRegisteredCommands() {
        return commandMap.values();
    }

    public abstract void execute(CommandSender sender, String label, String[] args);

    public abstract List<String> tabComplete(CommandSender sender, String alias, String[] args);
}
