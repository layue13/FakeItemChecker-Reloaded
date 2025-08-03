package com.layue13.fakeitemcheckerreloaded.config;

import com.layue13.fakeitemcheckerreloaded.ban.BanMethod;
import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.logging.Logger;

@Data
public class PluginConfig {
    private DatabaseConfig database;
    private List<String> ignoredInventories;
    private BanMethod banMode;
    private String banReason;
    private int checkDelay;
    private boolean debugMode;
    
    public static PluginConfig load(FileConfiguration config, Logger logger) {
        PluginConfig pluginConfig = new PluginConfig();
        
        // Database configuration
        ConfigurationSection dbSection = config.getConfigurationSection("database");
        if (dbSection != null) {
            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setUrl(dbSection.getString("url", "jdbc:mysql://localhost:3306/fic"));
            dbConfig.setUsername(dbSection.getString("username", "root"));
            dbConfig.setPassword(dbSection.getString("password", ""));
            dbConfig.setMaxPoolSize(dbSection.getInt("max_pool_size", 10));
            dbConfig.setMinIdle(dbSection.getInt("min_idle", 5));
            pluginConfig.setDatabase(dbConfig);
        } else {
            // Fallback for old config format
            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setUrl(config.getString("db.url", "jdbc:mysql://localhost:3306/fic"));
            dbConfig.setUsername(config.getString("db.username", "root"));
            dbConfig.setPassword(config.getString("db.password", ""));
            dbConfig.setMaxPoolSize(10);
            dbConfig.setMinIdle(5);
            pluginConfig.setDatabase(dbConfig);
        }
        
        // Other configurations
        pluginConfig.setIgnoredInventories(config.getStringList("ignored_inventories"));
        pluginConfig.setBanReason(config.getString("ban_reason", "你被ban了"));
        pluginConfig.setCheckDelay(config.getInt("check_delay", 1));
        pluginConfig.setDebugMode(config.getBoolean("debug_mode", false));
        
        // Ban mode configuration
        String banModeStr = config.getString("ban_mode", "BUKKIT_BAN");
        try {
            pluginConfig.setBanMode(BanMethod.valueOf(banModeStr));
        } catch (IllegalArgumentException e) {
            logger.warning("Invalid ban_mode: " + banModeStr + ". Using BUKKIT_BAN as default.");
            pluginConfig.setBanMode(BanMethod.BUKKIT_BAN);
        }
        
        return pluginConfig;
    }
    
    @Data
    public static class DatabaseConfig {
        private String url;
        private String username;
        private String password;
        private int maxPoolSize;
        private int minIdle;
    }
}