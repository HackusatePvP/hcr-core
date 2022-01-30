package dev.hcr.hcf;

import dev.hcr.hcf.factions.commands.member.DefaultFactionCommand;
import dev.hcr.hcf.factions.commands.FactionCommandManager;
import dev.hcr.hcf.factions.types.SafeZoneFaction;
import dev.hcr.hcf.listeners.UserListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class HCF extends JavaPlugin {
    private static HCF plugin;

    @Override
    public void onEnable() {
        plugin = this;
        registerCommands();
        loadFactions();
        Bukkit.getPluginManager().registerEvents(new UserListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerCommands() {
        new FactionCommandManager();
        getCommand("faction").setExecutor(new DefaultFactionCommand());
    }

    private void loadFactions() {
        new SafeZoneFaction("safezone");
    }

    public static HCF getPlugin() {
        return plugin;
    }
}
