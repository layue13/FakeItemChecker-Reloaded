package com.layue13.fakeitemcheckerreloaded;

import com.layue13.fakeitemcheckerreloaded.command.AdminCommand;
import com.layue13.fakeitemcheckerreloaded.hooks.PlayerDataSQLHook;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public final class FakeItemCheckerReloaded extends JavaPlugin {

    @Override
    public void onLoad() {
//        saveResource();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

        Bukkit.getServer().getPluginCommand("fic").setExecutor(new AdminCommand(this));

    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}