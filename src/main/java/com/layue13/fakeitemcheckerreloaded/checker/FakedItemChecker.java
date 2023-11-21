package com.layue13.fakeitemcheckerreloaded.checker;

import com.google.common.base.Preconditions;
import com.layue13.fakeitemcheckerreloaded.entity.Rule;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.function.Function;

public class FakedItemChecker {
    private final Collection<Rule> rules;
    private final Collection<String> ignoredInventoryTitles;
    private final Function<Player, Void> doAfterError;

    public FakedItemChecker(Collection<Rule> rules, Collection<String> ignoredInventoryTitles, Function<Player, Void> doAfterError) {
        Preconditions.checkNotNull(rules);
        Preconditions.checkNotNull(doAfterError);
        this.doAfterError = doAfterError;
        this.rules = rules;
        this.ignoredInventoryTitles = ignoredInventoryTitles;
    }

    public void check(Player holder, ItemStack itemStack) {
        rules.forEach(rule -> {
            if (!holder.hasPermission(rule.getPermission())) {
                String item = itemStack.getType().name() + ":" + itemStack.getDurability();
                if (rule.getItem().equals(item)) {
                    this.doAfterError.apply(holder);
                }
            }
        });
    }

    public void check(Player holder, Inventory inventory) {
        if (ignoredInventoryTitles.contains(inventory.getTitle())) {
            return;
        }
        inventory.forEach(itemStack -> {
            check(holder, itemStack);
        });
    }
}
