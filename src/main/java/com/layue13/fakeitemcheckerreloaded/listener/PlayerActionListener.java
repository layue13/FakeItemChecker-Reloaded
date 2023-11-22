package com.layue13.fakeitemcheckerreloaded.listener;

import com.layue13.fakeitemcheckerreloaded.FakeItemCheckerPlugin;
import com.layue13.fakeitemcheckerreloaded.ban.BanInfo;
import com.layue13.fakeitemcheckerreloaded.checker.FakedItemChecker;
import com.layue13.fakeitemcheckerreloaded.entity.Log;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

public class PlayerActionListener implements Listener {
    private final FakeItemCheckerPlugin plugin;
    private final FakedItemChecker checker;

    private final Collection<String> ignoredInventoryTitles;

    public PlayerActionListener(FakeItemCheckerPlugin plugin) {
        this.plugin = plugin;
        this.checker = new FakedItemChecker(plugin.getRuleRepository());
        ignoredInventoryTitles = plugin.getConfig().getStringList("ignored_inventory");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryOpenEvent(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        checkInventory(player, event, event.getInventory());
        checkInventory(player, event, player.getInventory());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        checkInventory(player, event, player.getInventory());
    }

    public void checkInventory(Player player, Event event, Inventory inventory) {
        if (this.ignoredInventoryTitles.contains(inventory.getTitle())) {
            return;
        }
        ItemStack[] itemStacks = inventory.getContents().clone();
        String eventName = event.getEventName();
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> checker.check(player, itemStacks, (p, rule) -> {
            Log log = Log.builder()
                    .id(UUID.randomUUID())
                    .playerName(p.getName())
                    .time(new Date())
                    .server(this.plugin.getServer().getServerId())
                    .location(p.getLocation().toString())
                    .event(eventName)
                    .inventoryType(inventory.getType())
                    .rule(rule)
                    .build();
            plugin.getLogRepository().save(log);
            plugin.getLogger().info(log.toString());
            BanInfo banInfo = BanInfo.builder()
                    .player(p)
                    .reason(log.toString())
                    .server(plugin.getServer().getServerName())
                    .source(plugin.getName())
                    .build();
            this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
                this.plugin.getBanMethod().ban(banInfo);
                p.kickPlayer(banInfo.toString());
            });
        }));
    }
}
