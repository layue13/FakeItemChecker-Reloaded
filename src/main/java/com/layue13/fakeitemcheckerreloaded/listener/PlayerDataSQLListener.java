package com.layue13.fakeitemcheckerreloaded.listener;

import cc.bukkitPlugin.pds.events.PlayerDataLoadCompleteEvent;
import com.layue13.fakeitemcheckerreloaded.FakeItemCheckerPlugin;
import com.layue13.fakeitemcheckerreloaded.ban.BanInfo;
import com.layue13.fakeitemcheckerreloaded.entity.Log;
import com.layue13.fakeitemcheckerreloaded.entity.Rule;
import com.layue13.fakeitemcheckerreloaded.service.ItemCheckService;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

@Slf4j
public class PlayerDataSQLListener implements Listener {
    private final FakeItemCheckerPlugin plugin;
    private final ItemCheckService checkService;
    private final Collection<String> ignoredInventoryTitles;

    public PlayerDataSQLListener(FakeItemCheckerPlugin plugin) {
        this.plugin = plugin;
        this.checkService = plugin.getItemCheckService();
        this.ignoredInventoryTitles = plugin.getPluginConfig().getIgnoredInventories();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDataLoadCompleteEvent(PlayerDataLoadCompleteEvent event) {
        checkInventory(event.getPlayer(), event, event.getPlayer().getInventory());
        checkInventory(event.getPlayer(), event, event.getPlayer().getEnderChest());
    }

    public void checkInventory(Player player, Event event, Inventory inventory) {
        if (this.ignoredInventoryTitles.contains(inventory.getTitle())) {
            return;
        }
        
        ItemStack[] itemStacks = inventory.getContents().clone();
        String eventName = event.getEventName();
        
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                checkService.check(player, itemStacks, (p, rule) -> handleViolation(p, rule, eventName, inventory));
            } catch (Exception e) {
                log.error("Error during PlayerDataSQL inventory check for player: " + player.getName(), e);
            }
        });
    }
    
    private void handleViolation(Player player, Rule rule, String eventName, Inventory inventory) {
        try {
            // Create log entry
            Log logEntry = Log.builder()
                    .id(UUID.randomUUID().toString())
                    .playerName(player.getName())
                    .time(new Date())
                    .server(this.plugin.getServer().getServerId())
                    .location(player.getLocation().toString())
                    .event(eventName)
                    .inventoryType(inventory.getType().name())
                    .rule(rule)
                    .build();
            
            // Create ban info
            BanInfo banInfo = BanInfo.builder()
                    .player(player)
                    .reason(plugin.getPluginConfig().getBanReason())
                    .server(plugin.getServer().getServerName())
                    .source(plugin.getName())
                    .plugin(plugin)
                    .build();
            
            // Execute ban and logging on main thread
            this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
                try {
                    // Execute ban
                    this.plugin.getBanMethod().ban(banInfo);
                    player.kickPlayer(banInfo.toString());
                    
                    // Save log
                    plugin.getLogRepository().save(logEntry);
                    log.info("Player {} violated rule via PlayerDataSQL: {} (Event: {}, Inventory: {})", 
                            player.getName(), rule, eventName, inventory.getType());
                    
                } catch (Exception e) {
                    log.error("Error handling PlayerDataSQL violation for player: " + player.getName(), e);
                }
            });
            
        } catch (Exception e) {
            log.error("Error creating PlayerDataSQL violation handler for player: " + player.getName(), e);
        }
    }
}
