package com.layue13.fakeitemcheckerreloaded;

import com.layue13.fakeitemcheckerreloaded.command.AdminCommand;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public final class FakeItemCheckerReloaded extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.saveDefaultConfig();
        this.getLogger().info("牛肉面君 QQ:382815711");
        this.getLogger().info("");
        Optional.ofNullable(Bukkit.getPluginManager().getPlugin("PlayerDataSQL"))
                .ifPresent(c -> {
                    this.getLogger().info(String.format("Plugin: %s has been detected.", c.getDescription().getName()));
                });

        Bukkit.getServer().getPluginCommand("fic").setExecutor(new AdminCommand(this));
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}