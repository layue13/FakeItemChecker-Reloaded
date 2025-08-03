package com.layue13.fakeitemcheckerreloaded.listener;

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
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

@Slf4j
public class PlayerActionListener implements Listener {
    
    private final FakeItemCheckerPlugin plugin;
    private final ItemCheckService checkService;
    private final Collection<String> ignoredInventoryTitles;
    private final int checkDelay;
    
    public PlayerActionListener(FakeItemCheckerPlugin plugin, ItemCheckService checkService) {
        this.plugin = plugin;
        this.checkService = checkService;
        this.ignoredInventoryTitles = plugin.getPluginConfig().getIgnoredInventories();
        this.checkDelay = plugin.getPluginConfig().getCheckDelay();
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryOpenEvent(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getPlayer();
        
        // Check player inventory
        scheduleInventoryCheck(player, event, player.getInventory());
        // Check opened inventory
        scheduleInventoryCheck(player, event, event.getInventory());
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getPlayer();
        
        if (player.isOnline()) {
            // Check player inventory
            scheduleInventoryCheck(player, event, player.getInventory());
            // Check closed inventory
            scheduleInventoryCheck(player, event, event.getInventory());
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        scheduleInventoryCheck(player, event, player.getInventory());
    }
    
    private void scheduleInventoryCheck(Player player, Event event, Inventory inventory) {
        if (!player.isOnline()) {
            return;
        }
        
        if (isIgnoredInventory(inventory)) {
            return;
        }
        
        // Run check asynchronously with delay
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, 
            () -> performInventoryCheck(player, event, inventory), checkDelay);
    }
    
    private boolean isIgnoredInventory(Inventory inventory) {
        String title = inventory.getTitle();
        return title != null && ignoredInventoryTitles.contains(title);
    }
    
    private void performInventoryCheck(Player player, Event event, Inventory inventory) {
        if (!player.isOnline()) {
            return;
        }
        
        try {
            ItemStack[] itemStacks = inventory.getContents();
            String eventName = event.getEventName();
            
            checkService.check(player, itemStacks, (p, rule) -> handleViolation(p, rule, eventName, inventory));
            
        } catch (Exception e) {
            log.error("Error during inventory check for player: " + player.getName(), e);
        }
    }
    
    private void handleViolation(Player player, Rule rule, String eventName, Inventory inventory) {
        try {
            // Create log entry
            Log logEntry = Log.builder()
                    .id(UUID.randomUUID().toString())
                    .playerName(player.getName())
                    .time(new Date())
                    .server(plugin.getServer().getServerName())
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
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                try {
                    // Save log
                    plugin.getLogRepository().save(logEntry);
                    log.info("Player {} violated rule: {} (Event: {}, Inventory: {})", 
                            player.getName(), rule, eventName, inventory.getType());
                    
                    // Execute ban
                    plugin.getBanMethod().ban(banInfo);
                    player.kickPlayer(banInfo.toString());
                    
                } catch (Exception e) {
                    log.error("Error handling violation for player: " + player.getName(), e);
                }
            });
            
        } catch (Exception e) {
            log.error("Error creating violation handler for player: " + player.getName(), e);
        }
    }
}
