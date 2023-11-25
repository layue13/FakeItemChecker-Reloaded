package com.layue13.fakeitemcheckerreloaded;

import com.layue13.fakeitemcheckerreloaded.ban.BanMethod;
import com.layue13.fakeitemcheckerreloaded.command.AdminCommand;
import com.layue13.fakeitemcheckerreloaded.dao.CachedRuleRepository;
import com.layue13.fakeitemcheckerreloaded.dao.LogRepository;
import com.layue13.fakeitemcheckerreloaded.dao.RuleRepository;
import com.layue13.fakeitemcheckerreloaded.listener.PlayerActionListener;
import com.layue13.fakeitemcheckerreloaded.listener.PlayerDataSQLListener;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;
import java.util.Optional;

public final class FakeItemCheckerPlugin extends JavaPlugin {
    private DataSource dataSource;
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

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(getConfig().getString("db.url"));
        config.setUsername(getConfig().getString("db.username"));
        config.setPassword(getConfig().getString("db.password"));
        this.dataSource = new HikariDataSource(config);


        Optional<BanMethod> optionalBanMethod = Optional.of(BanMethod.valueOf(this.getConfig().getString("ban_mode")));
        banMethod = optionalBanMethod.orElse(BanMethod.BUKKIT_BAN);
        if (banMethod.equals(BanMethod.BUNGEE_BAN)) {
            this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeBan");
        }

        ruleRepository = new CachedRuleRepository(dataSource);
        logRepository = new LogRepository(dataSource);
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
        ((HikariDataSource) this.dataSource).close();
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        HandlerList.unregisterAll(this);
    }

}