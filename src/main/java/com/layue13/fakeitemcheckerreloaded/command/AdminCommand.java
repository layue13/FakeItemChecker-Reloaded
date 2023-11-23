package com.layue13.fakeitemcheckerreloaded.command;

import com.layue13.fakeitemcheckerreloaded.FakeItemCheckerPlugin;
import com.layue13.fakeitemcheckerreloaded.entity.Rule;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;

import java.util.Arrays;


public class AdminCommand implements CommandExecutor {
    private final FakeItemCheckerPlugin plugin;

    public AdminCommand(FakeItemCheckerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!label.equalsIgnoreCase("fic")) {
            return false;
        }
        if (args.length == 0) {
            Arrays.asList(ChatColor.translateAlternateColorCodes('&', "&e/fic reload"), ChatColor.translateAlternateColorCodes('&', "&e/fic add")).forEach(sender::sendMessage);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "reload":
                this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
                this.plugin.getServer().getPluginManager().enablePlugin(this.plugin);
                sender.sendMessage(this.plugin.getName() + " has been reloaded, please check your console if throws exception.");
                break;
            case "add":
                if (!sender.isOp()) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have permission!"));
                    return false;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage("You should send the command as player.");
                    return false;
                }
                Player player = (Player) sender;
                ItemStack itemInHand = player.getItemInHand();
                if (itemInHand.getType().equals(Material.AIR)) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou can't add AIR!"));
                    return false;
                }
                Rule rule = Rule.builder().item(itemInHand.getType().toString() + ":" + itemInHand.getDurability() + ":" + args[1]).permission(new Permission(args[2])).build();
                this.plugin.getRuleRepository().save(rule);
                sender.sendMessage(String.format("Saved %s to repository.", rule.toString()));
                break;
            default:
                Arrays.asList(ChatColor.translateAlternateColorCodes('&', "&e/fic reload"), ChatColor.translateAlternateColorCodes('&', "&e/fic add <amount> <permission>")).forEach(sender::sendMessage);
                break;
        }
        return true;
    }
}