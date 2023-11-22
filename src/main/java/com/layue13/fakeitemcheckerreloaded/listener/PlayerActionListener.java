package com.layue13.fakeitemcheckerreloaded.listener;

import com.layue13.fakeitemcheckerreloaded.FakeItemCheckerReloaded;
import com.layue13.fakeitemcheckerreloaded.ban.BanInfo;
import com.layue13.fakeitemcheckerreloaded.ban.BanMethod;
import com.layue13.fakeitemcheckerreloaded.checker.FakedItemChecker;
import com.layue13.fakeitemcheckerreloaded.entity.Log;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

public class PlayerActionListener implements Listener {
    private final FakeItemCheckerReloaded plugin;
    private final FakedItemChecker checker;

    private final Collection<String> ignoredInventoryTitles;

    public PlayerActionListener(FakeItemCheckerReloaded plugin) {
        this.plugin = plugin;
        this.checker = new FakedItemChecker(plugin.getRuleRepository());
        ignoredInventoryTitles = plugin.getConfig().getStringList("ignored_inventory");
    }

//    @EventHandler(priority = EventPriority.HIGHEST)
//    public void onPlayerDataLoadCompleteEvent(PlayerDataLoadCompleteEvent event) {
//        checkInventory(event, event.getPlayer(), event.getPlayer().getInventory());
//        // check player's ender chest.
//        checkInventory(event, event.getPlayer(), event.getPlayer().getEnderChest());
//    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryOpenEvent(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();
        if (this.ignoredInventoryTitles.contains(inventory.getTitle())) {
            return;
        }
        ItemStack[] itemStacks = inventory.getContents().clone();
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            checker.check((Player) event.getPlayer(), itemStacks, (player, rule) -> {
                Log log = Log.builder()
                        .id(UUID.randomUUID())
                        .playerName(player.getName())
                        .time(new Date())
                        .server(this.plugin.getServer().getServerId())
                        .location(player.getLocation().toString())
                        .event(event.getEventName())
                        .inventoryType(inventory.getType())
                        .rule(rule)
                        .build();
                plugin.getLogRepository().save(log);
                plugin.getLogger().info(log.toString());
                BanInfo banInfo = BanInfo.builder()
                        .player(player)
                        .reason(log.toString())
                        .server(plugin.getServer().getServerName())
                        .source(plugin.getName())
                        .build();
                this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
                    BanMethod.BUKKIT_BAN.ban(banInfo);
                    player.kickPlayer(banInfo.toString());
                });
            });
        });
    }
}
