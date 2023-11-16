package com.layue13.fakeitemcheckerreloaded.command;

import com.layue13.fakeitemcheckerreloaded.FakeItemCheckerReloaded;
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

        return true;
    }
}