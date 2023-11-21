package com.layue13.fakeitemcheckerreloaded;

import com.layue13.fakeitemcheckerreloaded.command.AdminCommand;
import com.layue13.fakeitemcheckerreloaded.dao.LogRepository;
import com.layue13.fakeitemcheckerreloaded.dao.RuleRepository;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class FakeItemCheckerReloaded extends JavaPlugin {
    private Connection connection;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        reloadConfig();

        try {
            connection = DriverManager.getConnection(getConfig().getString("db.url"), getConfig().getString("db.username"), getConfig().getString("db.password"));
        } catch (SQLException e) {
            this.getServer().shutdown();
            throw new RuntimeException(e);
        }

        RuleRepository ruleRepository = new RuleRepository(connection);
        LogRepository logRepository = new LogRepository(connection);
        ruleRepository.init();
        logRepository.init();
        Bukkit.getServer().getPluginCommand("fic").setExecutor(new AdminCommand(this));
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}