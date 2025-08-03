package com.layue13.fakeitemcheckerreloaded.command;

import com.layue13.fakeitemcheckerreloaded.FakeItemCheckerPlugin;
import com.layue13.fakeitemcheckerreloaded.entity.Rule;
import com.layue13.fakeitemcheckerreloaded.service.ItemCheckService;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class AdminCommand implements CommandExecutor, TabCompleter {
    
    private final FakeItemCheckerPlugin plugin;
    private final ItemCheckService checkService;
    
    private static final List<String> SUBCOMMANDS = Arrays.asList("reload", "add", "list", "remove", "info");
    
    public AdminCommand(FakeItemCheckerPlugin plugin, ItemCheckService checkService) {
        this.plugin = plugin;
        this.checkService = checkService;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!label.equalsIgnoreCase("fic")) {
            return false;
        }
        
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }
        
        String subcommand = args[0].toLowerCase();
        
        try {
            switch (subcommand) {
                case "reload":
                    return handleReload(sender);
                case "add":
                    return handleAdd(sender, args);
                case "list":
                    return handleList(sender);
                case "remove":
                    return handleRemove(sender, args);
                case "info":
                    return handleInfo(sender);
                default:
                    sendHelpMessage(sender);
                    return true;
            }
        } catch (Exception e) {
            log.error("Error executing command: " + String.join(" ", args), e);
            sender.sendMessage(ChatColor.RED + "An error occurred while executing the command. Check console for details.");
            return true;
        }
    }
    
    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("fic.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }
        
        try {
            plugin.reloadPlugin();
            checkService.refreshCache();
            sender.sendMessage(ChatColor.GREEN + "Plugin reloaded successfully!");
            log.info("Plugin reloaded by: " + sender.getName());
        } catch (Exception e) {
            log.error("Failed to reload plugin", e);
            sender.sendMessage(ChatColor.RED + "Failed to reload plugin. Check console for details.");
        }
        return true;
    }
    
    private boolean handleAdd(CommandSender sender, String[] args) {
        if (!sender.hasPermission("fic.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command must be executed by a player!");
            return true;
        }
        Player player = (Player) sender;
        
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /fic add <amount> <permission>");
            return true;
        }
        
        try {
            ItemStack itemInHand = player.getItemInHand();
            if (itemInHand.getType() == Material.AIR) {
                sender.sendMessage(ChatColor.RED + "You must hold an item in your hand!");
                return true;
            }
            
            int amount = Integer.parseInt(args[1]);
            if (amount <= 0) {
                sender.sendMessage(ChatColor.RED + "Amount must be positive!");
                return true;
            }
            
            String permission = args[2];
            if (permission.trim().isEmpty()) {
                sender.sendMessage(ChatColor.RED + "Permission cannot be empty!");
                return true;
            }
            
            Rule rule = Rule.builder()
                    .item(itemInHand.getType().toString() + ":" + itemInHand.getDurability() + ":" + amount)
                    .permission(permission)
                    .build();
            
            if (!rule.isValid()) {
                sender.sendMessage(ChatColor.RED + "Invalid rule: " + rule.getValidationError());
                return true;
            }
            
            Rule savedRule = plugin.getRuleRepository().save(rule);
            checkService.addRule(savedRule);
            
            sender.sendMessage(ChatColor.GREEN + "Rule added successfully: " + savedRule);
            log.info("Rule added by {}: {}", sender.getName(), savedRule);
            
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid amount: " + args[1]);
        } catch (Exception e) {
            log.error("Error adding rule", e);
            sender.sendMessage(ChatColor.RED + "Failed to add rule. Check console for details.");
        }
        
        return true;
    }
    
    private boolean handleList(CommandSender sender) {
        if (!sender.hasPermission("fic.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }
        
        try {
            Collection<Rule> rules = plugin.getRuleRepository().findAll();
            if (rules.isEmpty()) {
                sender.sendMessage(ChatColor.YELLOW + "No rules found.");
                return true;
            }
            
            sender.sendMessage(ChatColor.GREEN + "Rules (" + rules.size() + "):");
            for (Rule rule : rules) {
                sender.sendMessage(ChatColor.WHITE + "- " + rule);
            }
            
        } catch (Exception e) {
            log.error("Error listing rules", e);
            sender.sendMessage(ChatColor.RED + "Failed to list rules. Check console for details.");
        }
        
        return true;
    }
    
    private boolean handleRemove(CommandSender sender, String[] args) {
        if (!sender.hasPermission("fic.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /fic remove <rule_id>");
            return true;
        }
        
        try {
            long ruleId = Long.parseLong(args[1]);
            Optional<Rule> rule = plugin.getRuleRepository().findById(ruleId);
            
            if (!rule.isPresent()) {
                sender.sendMessage(ChatColor.RED + "Rule not found with ID: " + ruleId);
                return true;
            }
            
            plugin.getRuleRepository().deleteById(ruleId);
            checkService.removeRule(rule.get());
            
            sender.sendMessage(ChatColor.GREEN + "Rule removed successfully: " + rule.get());
            log.info("Rule removed by {}: {}", sender.getName(), rule.get());
            
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid rule ID: " + args[1]);
        } catch (Exception e) {
            log.error("Error removing rule", e);
            sender.sendMessage(ChatColor.RED + "Failed to remove rule. Check console for details.");
        }
        
        return true;
    }
    
    private boolean handleInfo(CommandSender sender) {
        if (!sender.hasPermission("fic.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }
        
        try {
            long ruleCount = plugin.getRuleRepository().count();
            int cachedRuleCount = checkService.getCachedRuleCount();
            boolean cacheInitialized = checkService.isCacheInitialized();
            
            sender.sendMessage(ChatColor.GREEN + "Plugin Information:");
            sender.sendMessage(ChatColor.WHITE + "- Total Rules: " + ruleCount);
            sender.sendMessage(ChatColor.WHITE + "- Cached Rules: " + cachedRuleCount);
            sender.sendMessage(ChatColor.WHITE + "- Cache Initialized: " + cacheInitialized);
            sender.sendMessage(ChatColor.WHITE + "- Ban Mode: " + plugin.getPluginConfig().getBanMode());
            
        } catch (Exception e) {
            log.error("Error getting plugin info", e);
            sender.sendMessage(ChatColor.RED + "Failed to get plugin info. Check console for details.");
        }
        
        return true;
    }
    
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "FakeItemChecker Commands:");
        sender.sendMessage(ChatColor.WHITE + "/fic reload - Reload the plugin");
        sender.sendMessage(ChatColor.WHITE + "/fic add <amount> <permission> - Add a new rule");
        sender.sendMessage(ChatColor.WHITE + "/fic list - List all rules");
        sender.sendMessage(ChatColor.WHITE + "/fic remove <id> - Remove a rule");
        sender.sendMessage(ChatColor.WHITE + "/fic info - Show plugin information");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("fic.admin")) {
            return new ArrayList<>();
        }
        
        if (args.length == 1) {
            return SUBCOMMANDS.stream()
                    .filter(sub -> sub.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        if (args.length == 2 && "remove".equals(args[0].toLowerCase())) {
            try {
                Collection<Rule> rules = plugin.getRuleRepository().findAll();
                return rules.stream()
                        .map(rule -> String.valueOf(rule.getId()))
                        .filter(id -> id.startsWith(args[1]))
                        .collect(Collectors.toList());
            } catch (Exception e) {
                log.error("Error getting tab completion for remove command", e);
            }
        }
        
        return new ArrayList<>();
    }
}