package com.layue13.fakeitemcheckerreloaded;

import com.layue13.fakeitemcheckerreloaded.command.AdminCommand;
import com.layue13.fakeitemcheckerreloaded.dao.LogRepository;
import com.layue13.fakeitemcheckerreloaded.dao.RuleRepository;
import com.layue13.fakeitemcheckerreloaded.listener.PlayerActionListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class FakeItemCheckerReloaded extends JavaPlugin {
    private Connection connection;
    @Getter
    private RuleRepository ruleRepository;
    @Getter
    private LogRepository logRepository;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        reloadConfig();

        try {
            connection = DriverManager.getConnection(getConfig().getString("db.url"), getConfig().getString("db.username"), getConfig().getString("db.password"));
        } catch (SQLException e) {
            this.getLogger().warning("Please confirm your database info is configured.");
            this.getServer().shutdown();
            throw new RuntimeException(e);
        }

        ruleRepository = new RuleRepository(connection);
        logRepository = new LogRepository(connection);
        ruleRepository.init();
        logRepository.init();

        Bukkit.getServer().getPluginCommand("fic").setExecutor(new AdminCommand(this));
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerActionListener(this), this);
    }

    @Override
    public void onDisable() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        HandlerList.unregisterAll(this);
    }

}