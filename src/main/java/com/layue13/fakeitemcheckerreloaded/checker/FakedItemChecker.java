package com.layue13.fakeitemcheckerreloaded.checker;

import com.google.common.base.Preconditions;
import com.layue13.fakeitemcheckerreloaded.dao.RuleRepository;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.function.Function;

public class FakedItemChecker {
    private final RuleRepository ruleRepository;
    private final Collection<String> ignoredInventoryTitles;

    public FakedItemChecker(RuleRepository ruleRepository, Collection<String> ignoredInventoryTitles, Function<Player, Void> doAfterError) {
        Preconditions.checkNotNull(ruleRepository);
        Preconditions.checkNotNull(doAfterError);
        this.ruleRepository = ruleRepository;
        this.ignoredInventoryTitles = ignoredInventoryTitles;
    }

    public void check(Player holder, ItemStack itemStack, Function<Player, Void> doAfterIfError) {
        ruleRepository.getAll().forEach(rule -> {
            if (!holder.hasPermission(rule.getPermission())) {
                String item = itemStack.getType().name() + ":" + itemStack.getDurability();
                if (rule.getItem().equals(item)) {
                    doAfterIfError.apply(holder);
                }
            }
        });
    }

    public void check(Player holder, Inventory inventory, Function<Player, Void> doAfterIfError) {
        if (ignoredInventoryTitles.contains(inventory.getTitle())) {
            return;
        }
        inventory.forEach(itemStack -> {
            check(holder, itemStack, doAfterIfError);
        });
    }
}
