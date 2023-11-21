package com.layue13.fakeitemcheckerreloaded.command;

import com.layue13.fakeitemcheckerreloaded.FakeItemCheckerReloaded;
import com.layue13.fakeitemcheckerreloaded.entity.Rule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;

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
        if (!(sender instanceof Player)) {
            sender.sendMessage("You should send the command as player.");
            return false;
        }

        Player player = (Player) sender;
        switch (Optional.of(args[0]).orElse("").toLowerCase()) {
            case "reload":
                Bukkit.getServer().getPluginManager().disablePlugin(this.plugin);
                Bukkit.getServer().getPluginManager().enablePlugin(this.plugin);
                break;
            case "add":
                if (!sender.isOp()) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have permission!"));
                    return false;
                }
                ItemStack itemInHand = player.getItemInHand();
                if (itemInHand.getType().equals(Material.AIR)) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou can't add AIR!"));
                    return false;
                }
                this.plugin.getRuleRepository().save(Rule.builder().item(itemInHand.getType().toString() + ":" + itemInHand.getDurability()).permission(new Permission(args[1])).build());
                sender.sendMessage("Saved");
                break;
            default:
                Arrays.asList(ChatColor.translateAlternateColorCodes('&', "&e/fic reload"), ChatColor.translateAlternateColorCodes('&', "&e/fic add")).forEach(sender::sendMessage);
                break;
        }
        return true;
    }
}