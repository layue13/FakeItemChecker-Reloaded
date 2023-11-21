package com.layue13.fakeitemcheckerreloaded.listener;

import cc.bukkitPlugin.pds.events.PlayerDataLoadCompleteEvent;
import com.layue13.fakeitemcheckerreloaded.FakeItemCheckerReloaded;
import com.layue13.fakeitemcheckerreloaded.checker.FakedItemChecker;
import com.layue13.fakeitemcheckerreloaded.entity.Log;
import net.craftminecraft.bungee.bungeeban.BanManager;
import net.craftminecraft.bungee.bungeeban.banstore.SimpleBanEntry;
import org.bukkit.Bukkit;
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

public class PlayerActionListener implements Listener {
    private final FakeItemCheckerReloaded plugin;
    private final FakedItemChecker checker;

    private Collection<String> ignoredInventoryTitles;

    public PlayerActionListener(FakeItemCheckerReloaded plugin) {
        this.plugin = plugin;
        this.checker = new FakedItemChecker(plugin.getRuleRepository());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDataLoadCompleteEvent(PlayerDataLoadCompleteEvent event) {
        checkInventory(event, event.getPlayer(), event.getPlayer().getInventory());
    }

    private void checkInventory(Event event, Player player, Inventory inventory) {
        ItemStack[] inventoryItemStacks = inventory.getContents().clone();
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            checker.check(player, inventoryItemStacks, p -> {
                Log log = Log.builder()
                        .id(UUID.randomUUID())
                        .playerName(player.getName())
                        .time(new Date())
                        .server(this.plugin.getServer().getServerName())
                        .location(player.getLocation().toString())
                        .event(event.getEventName())
                        .inventoryType(inventory.getType())
                        .build();
                this.plugin.getLogRepository().save(log);
                Bukkit.getScheduler().runTask(this.plugin, () -> {
                    BanManager.ban(new SimpleBanEntry.Builder()
                            .banned("")
                            .reason(log.toString())
                            .created(log.getTime())
                            .expiry()
                            .global()
                            .source("(FakedItemChecker)")
                            .build());
                });
                return null;
            });
        });
    }
}
