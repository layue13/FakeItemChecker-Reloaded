//package com.layue13.fakeitemcheckerreloaded.listener;
//
//import java.io.*;
//
//import cc.bukkitPlugin.pds.events.PlayerDataLoadCompleteEvent;
//import net.minecraft.server.v1_7_R4.TileEntity;
//import org.bukkit.configuration.file.*;
//
//import java.util.*;
//import java.util.logging.Logger;
//
//import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
//import org.bukkit.entity.*;
//import org.bukkit.scheduler.*;
//import org.bukkit.*;
//import org.bukkit.inventory.*;
//import org.bukkit.block.*;
//import org.bukkit.event.*;
//import org.bukkit.event.inventory.*;
//import org.bukkit.event.player.*;
//import org.bukkit.event.block.*;
//
//public class PlayerActionListener implements Listener {
//    @EventHandler
//    public void onPlayerDataLoadCompleted(PlayerDataLoadCompleteEvent event) {
//        Bukkit.getScheduler().runTaskAsynchronously()
//    }
//
//    @EventHandler
//    public void onPlayerJoin(final PlayerJoinEvent e) {
//        Player player = e.getPlayer();
//        File playerDataFile = new File("./plugins/FakeItemChecker/PlayerData", player.getName() + ".yml");
//        if (!playerDataFile.exists()) {
//            if (!playerDataFile.getParentFile().exists()) {
//                playerDataFile.getParentFile().mkdirs();
//            }
//            try {
//                playerDataFile.createNewFile();
//                logger.info(String.format("Create a new data file for player: %s", player.getName()));
//            } catch (IOException ex) {
//                throw new RuntimeException(ex);
//            }
//        }
//        YamlConfiguration playerData = YamlConfiguration.loadConfiguration(playerDataFile);
//        playerData.addDefault("name", player.getName());
//        playerData.addDefault("list", new ArrayList<>());
//        try {
//            playerData.save(playerDataFile);
//        } catch (IOException ex) {
//            throw new RuntimeException(ex);
//        }
//        playerDataMap.put(playerData.getName(), playerData);
//        FICAPI.checkListTime(player);
//    }
//
//    @EventHandler
//    public void onPlayerLeave(final PlayerQuitEvent e) {
//        Player player = e.getPlayer();
//        File playerFile = new File("./plugins/FakeItemChecker/PlayerData", player.getName() + ".yml");
//        try {
//            playerDataMap.get(player.getName()).save(playerFile);
//            playerDataMap.remove(player.getName());
//        } catch (Exception ignored) {
//        }
//    }
//
//    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
//    public void onPlayerOpenInv(final InventoryOpenEvent e) {
//        final Inventory inv = e.getInventory();
//        if (!(inv.getHolder() instanceof BlockState) && Utils.checkInvName(inv)) {
//            return;
//        }
//        FICAPI.checkListTime((Player) e.getPlayer());
//        Location loc = e.getPlayer().getLocation();
//        if (inv.getHolder() instanceof BlockState) {
//            final Block b = ((BlockState) inv.getHolder()).getBlock();
//            loc = b.getLocation();
//        }
//        final String invname = inv.getName();
//        final Location floc = loc;
//        new BukkitRunnable(inv, e) {
//            final ItemStack[] checkitems = inv.getContents().clone();
//            final Player p = (Player) e.getPlayer();
//
//            public void run() {
//                FakedItemChecker.checkFakedItem(this.checkitems, this.p, invname, floc);
//            }
//        }.runTaskAsynchronously(Main.plugin);
//    }
//
//    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
//    public void onPlayerCloseInv(final InventoryCloseEvent e) {
//        final Inventory inv = e.getInventory();
//        if (!(inv.getHolder() instanceof BlockState) && Utils.checkInvName(inv)) {
//            return;
//        }
//        Location loc = e.getPlayer().getLocation();
//        if (inv.getHolder() instanceof BlockState) {
//            final Block b = ((BlockState) inv.getHolder()).getBlock();
//            loc = b.getLocation();
//        }
//        final String invname = inv.getName();
//        final Location floc = loc;
//        new BukkitRunnable(inv, e) {
//            final ItemStack[] checkitems = inventory.getContents().clone();
//            final Player p = (Player) inventoryCloseEvent.getPlayer();
//
//            public void run() {
//                FakedItemChecker.checkFakedItem(this.checkitems, this.p, invname, floc);
//            }
//        }.runTaskAsynchronously(Main.plugin);
//    }
//
//    public void onPlayerPick(final PlayerPickupItemEvent e) {
//        final Inventory inv = (Inventory) e.getPlayer().getInventory();
//        final Location loc = e.getPlayer().getLocation();
//        final String invname = inv.getName();
//        final Location floc = loc;
//        new BukkitRunnable(inv, e) {
//            final ItemStack[] checkitems = inventory.getContents().clone();
//            final Player p = playerPickupItemEvent.getPlayer();
//
//            public void run() {
//                FakedItemChecker.checkFakedItem(this.checkitems, this.p, invname, floc);
//            }
//        }.runTaskAsynchronously(Main.plugin);
//    }
//
//    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
//    public void onBlockBreak(final BlockBreakEvent e) {
//        final Block b = e.getBlock();
//        if (!(b.getState() instanceof BlockState)) {
//            return;
//        }
//        final Location loc = b.getState().getLocation();
//        final TileEntity tile = ((CraftWorld) b.getWorld()).getHandle().getTileEntity(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
//        if (tile.getOwner() == null) {
//            return;
//        }
//        if (tile.getOwner().getInventory() == null) {
//            return;
//        }
//        final Inventory inv = tile.getOwner().getInventory();
//        final String invname = inv.getName();
//        final Location floc = loc;
//        new BukkitRunnable(inv, e) {
//            final ItemStack[] checkitems = inventory.getContents().clone();
//            final Player p = blockBreakEvent.getPlayer();
//
//            public void run() {
//                FakedItemChecker.checkFakedItem(this.checkitems, this.p, invname, floc);
//            }
//        }.runTaskAsynchronously(Main.plugin);
//    }
//
//    public void onDisable(){
//
//    }
//}
