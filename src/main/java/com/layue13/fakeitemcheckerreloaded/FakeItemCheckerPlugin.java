package com.layue13.fakeitemcheckerreloaded;

import com.layue13.fakeitemcheckerreloaded.ban.BanMethod;
import com.layue13.fakeitemcheckerreloaded.command.AdminCommand;
import com.layue13.fakeitemcheckerreloaded.config.PluginConfig;
import com.layue13.fakeitemcheckerreloaded.database.DatabaseManager;
import com.layue13.fakeitemcheckerreloaded.listener.PlayerActionListener;
import com.layue13.fakeitemcheckerreloaded.listener.PlayerDataSQLListener;
import com.layue13.fakeitemcheckerreloaded.repository.LogRepository;
import com.layue13.fakeitemcheckerreloaded.repository.RuleRepository;
import com.layue13.fakeitemcheckerreloaded.service.ItemCheckService;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class FakeItemCheckerPlugin extends JavaPlugin {
    
    private PluginConfig pluginConfig;
    private DatabaseManager databaseManager;
    private RuleRepository ruleRepository;
    private LogRepository logRepository;
    private ItemCheckService itemCheckService;
    private BanMethod banMethod;
    
    @Override
    public void onEnable() {
        try {
            // Initialize configuration
            saveDefaultConfig();
            reloadConfig();
            this.pluginConfig = PluginConfig.load(getConfig(), getLogger());
            
            // Initialize database
            this.databaseManager = new DatabaseManager(pluginConfig.getDatabase(), getLogger());
            
            // Initialize repositories
            this.ruleRepository = new RuleRepository(databaseManager.getDataSource());
            this.logRepository = new LogRepository(databaseManager.getDataSource());
            
            // Initialize repositories
            ruleRepository.init();
            logRepository.init();
            
            // Initialize services
            this.itemCheckService = new ItemCheckService(ruleRepository);
            
            // Initialize ban method
            this.banMethod = pluginConfig.getBanMode();
            if (banMethod == BanMethod.BUNGEE_BAN) {
                getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeBan");
            }
            
            // Register listeners
            getServer().getPluginManager().registerEvents(
                new PlayerActionListener(this, itemCheckService), this);
            
            // Register PlayerDataSQL listener if available
            if (getServer().getPluginManager().getPlugin("PlayerDataSQL") != null) {
                getLogger().info("Hooking into PlayerDataSQL plugin");
                getServer().getPluginManager().registerEvents(
                    new PlayerDataSQLListener(this), this);
            }
            
            // Register commands
            AdminCommand adminCommand = new AdminCommand(this, itemCheckService);
            getServer().getPluginCommand("fic").setExecutor(adminCommand);
            getServer().getPluginCommand("fic").setTabCompleter(adminCommand);
            
            getLogger().info("FakeItemChecker-Reloaded v" + getDescription().getVersion() + " enabled successfully!");
            getLogger().info("Loaded " + ruleRepository.count() + " rules");
            getLogger().info("Ban mode: " + banMethod);
            
        } catch (Exception e) {
            getLogger().severe("Failed to enable plugin: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }
    
    @Override
    public void onDisable() {
        try {
            // Unregister plugin channels
            getServer().getMessenger().unregisterOutgoingPluginChannel(this);
            
            // Unregister all listeners
            HandlerList.unregisterAll(this);
            
            // Close database connections
            if (databaseManager != null) {
                databaseManager.close();
            }
            
            getLogger().info("FakeItemChecker-Reloaded disabled successfully!");
            
        } catch (Exception e) {
            getLogger().severe("Error during plugin shutdown: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void reloadPlugin() {
        try {
            // Reload configuration
            reloadConfig();
            this.pluginConfig = PluginConfig.load(getConfig(), getLogger());
            
            // Update ban method
            this.banMethod = pluginConfig.getBanMode();
            
            // Refresh item check service cache
            if (itemCheckService != null) {
                itemCheckService.refreshCache();
            }
            
            getLogger().info("Plugin configuration reloaded successfully!");
            
        } catch (Exception e) {
            getLogger().severe("Failed to reload plugin: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to reload plugin", e);
        }
    }
}