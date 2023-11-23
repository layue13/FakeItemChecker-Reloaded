package com.layue13.fakeitemcheckerreloaded;

import com.layue13.fakeitemcheckerreloaded.ban.BanMethod;
import com.layue13.fakeitemcheckerreloaded.command.AdminCommand;
import com.layue13.fakeitemcheckerreloaded.dao.LogRepository;
import com.layue13.fakeitemcheckerreloaded.dao.RuleRepository;
import com.layue13.fakeitemcheckerreloaded.listener.PlayerActionListener;
import com.layue13.fakeitemcheckerreloaded.listener.PlayerDataSQLListener;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

public final class FakeItemCheckerPlugin extends JavaPlugin {
    private Connection connection;
    @Getter
    private RuleRepository ruleRepository;
    @Getter
    private LogRepository logRepository;
    @Getter
    private BanMethod banMethod;

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

        String ban_mode = Optional.ofNullable(getConfig().getString("ban_mode"))
                .orElse("BUKKIT_BAN");
        this.banMethod = BanMethod.valueOf(ban_mode);
//        if (banMethod.equals(BanMethod.BUNGEE_BAN)) {
//            this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeBan");
//        }

        ruleRepository = new RuleRepository(connection);
        logRepository = new LogRepository(connection);
        ruleRepository.init();
        logRepository.init();

        Optional.ofNullable(this.getServer().getPluginManager().getPlugin("PlayerDataSQL")).ifPresent(plugin -> {
            this.getLogger().info("Hook to the plugin: " + plugin.getName() + " Version:" + plugin.getDescription().getVersion());
            this.getServer().getPluginManager().registerEvents(new PlayerDataSQLListener(this), this);
        });
        this.getServer().getPluginCommand("fic").setExecutor(new AdminCommand(this));
        this.getServer().getPluginManager().registerEvents(new PlayerActionListener(this), this);
    }

    @Override
    public void onDisable() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        HandlerList.unregisterAll(this);
    }

}