package com.layue13.fakeitemcheckerreloaded.service;

import com.layue13.fakeitemcheckerreloaded.entity.Rule;
import com.layue13.fakeitemcheckerreloaded.repository.Repository;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Slf4j
public class ItemCheckService {
    
    private final Repository<Rule, Long> ruleRepository;
    private final Map<String, Rule> ruleCache = new ConcurrentHashMap<>();
    private volatile boolean cacheInitialized = false;
    
    public ItemCheckService(Repository<Rule, Long> ruleRepository) {
        this.ruleRepository = ruleRepository;
        initializeCache();
    }
    
    private void initializeCache() {
        try {
            Collection<Rule> rules = ruleRepository.findAll();
            ruleCache.clear();
            for (Rule rule : rules) {
                String key = generateRuleKey(rule);
                ruleCache.put(key, rule);
            }
            cacheInitialized = true;
            log.info("Rule cache initialized with {} rules", rules.size());
        } catch (Exception e) {
            log.error("Failed to initialize rule cache", e);
            throw new RuntimeException("Failed to initialize rule cache", e);
        }
    }
    
    public void refreshCache() {
        cacheInitialized = false;
        initializeCache();
    }
    
    public void check(Player holder, ItemStack itemStack, BiConsumer<Player, Rule> violationHandler) {
        if (holder == null || itemStack == null || violationHandler == null) {
            log.warn("Invalid parameters for item check");
            return;
        }
        
        if (itemStack.getType() == Material.AIR) {
            return;
        }
        
        String itemKey = generateItemKey(itemStack);
        Rule matchingRule = ruleCache.get(itemKey);
        
        if (matchingRule != null) {
            if (!hasPermission(holder, matchingRule)) {
                log.debug("Player {} violated rule: {}", holder.getName(), matchingRule);
                violationHandler.accept(holder, matchingRule);
            }
        }
    }
    
    public void check(Player holder, ItemStack[] itemStacks, BiConsumer<Player, Rule> violationHandler) {
        if (holder == null || itemStacks == null || violationHandler == null) {
            log.warn("Invalid parameters for inventory check");
            return;
        }
        
        // Group items by type and durability, sum amounts
        Map<String, ItemStack> groupedItems = java.util.Arrays.stream(itemStacks)
                .filter(Objects::nonNull)
                .filter(item -> item.getType() != Material.AIR)
                .collect(Collectors.groupingBy(
                    this::generateItemKey,
                    Collectors.reducing(null, (a, b) -> {
                        if (a == null) return b;
                        if (b == null) return a;
                        a.setAmount(a.getAmount() + b.getAmount());
                        return a;
                    })
                ));
        
        // Check each unique item
        for (ItemStack itemStack : groupedItems.values()) {
            if (itemStack != null) {
                check(holder, itemStack, violationHandler);
            }
        }
    }
    
    private String generateItemKey(ItemStack itemStack) {
        return itemStack.getType().name() + ":" + itemStack.getDurability();
    }
    
    private String generateRuleKey(Rule rule) {
        try {
            ItemStack ruleItem = rule.getItemStack();
            return ruleItem.getType().name() + ":" + ruleItem.getDurability();
        } catch (Exception e) {
            log.error("Failed to generate rule key for rule: {}", rule, e);
            return null;
        }
    }
    
    private boolean hasPermission(Player player, Rule rule) {
        return player.hasPermission(rule.getPermission()) || player.isOp();
    }
    
    public boolean isCacheInitialized() {
        return cacheInitialized;
    }
    
    public int getCachedRuleCount() {
        return ruleCache.size();
    }
    
    public void addRule(Rule rule) {
        if (rule.isValid()) {
            String key = generateRuleKey(rule);
            if (key != null) {
                ruleCache.put(key, rule);
                log.debug("Added rule to cache: {}", rule);
            }
        } else {
            log.warn("Attempted to add invalid rule to cache: {}", rule);
        }
    }
    
    public void removeRule(Rule rule) {
        String key = generateRuleKey(rule);
        if (key != null) {
            ruleCache.remove(key);
            log.debug("Removed rule from cache: {}", rule);
        }
    }
}