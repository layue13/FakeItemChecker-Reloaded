package com.layue13.fakeitemcheckerreloaded.listener;

import com.layue13.fakeitemcheckerreloaded.FakeItemCheckerReloaded;
import com.layue13.fakeitemcheckerreloaded.checker.FakedItemChecker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.Collection;

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
        checker.checkInventory(this.plugin, event, (Player) event.getPlayer(), event.getInventory(), ignoredInventoryTitles);
    }
}
