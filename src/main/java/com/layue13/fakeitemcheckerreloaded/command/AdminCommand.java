package com.layue13.fakeitemcheckerreloaded.command;

import com.layue13.fakeitemcheckerreloaded.FakeItemCheckerReloaded;
import com.layue13.fakeitemcheckerreloaded.fakeditem.FakedItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Optional;


public class AdminCommand implements CommandExecutor {
    private final FakeItemCheckerReloaded plugin;

    public AdminCommand(FakeItemCheckerReloaded plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!label.equalsIgnoreCase("fic")) {
            return false;
        }
        switch (Optional.of(args[0]).orElse("").toLowerCase()) {
            case "reload":
                this.plugin.onDisable();
                this.plugin.onEnable();
                break;
            case "add":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThe command must be sent by player!"));
                    return false;
                }
                if (!sender.isOp()) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have permission!"));
                    return false;
                }
                ItemStack itemInHand = ((Player) sender).getItemInHand();
                if (itemInHand.getType().equals(Material.AIR)) {
                    return false;
                }
                FakedItem fakedItem = FakedItem.builder()
                        .item(itemInHand)
                        .rule(FakedItem.Rule.builder()
                                .logIfNum(1)
                                .errorIfNum(1)
                                .build())
                        .build();
                String itemName = CraftItemStack.asNMSCopy(((Player) sender).getItemInHand()).getName();
                Optional.ofNullable(this.plugin.getConfig().getConfigurationSection("items"))
                        .orElseGet(() -> this.plugin.getConfig().createSection("items")).createSection(itemName + "@" + System.currentTimeMillis(), fakedItem.serialize());
                this.plugin.saveConfig();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a????&r " + CraftItemStack.asNMSCopy(fakedItem.getItem()).getName() + " &a?????."));
                break;
            default:
                Arrays.asList(ChatColor.translateAlternateColorCodes('&', "&e/fic reload"),
                                ChatColor.translateAlternateColorCodes('&', "&e/fic add"))
                        .forEach(sender::sendMessage);
                break;
        }
        return true;
    }
}