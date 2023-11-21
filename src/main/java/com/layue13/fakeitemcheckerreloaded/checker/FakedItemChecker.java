package com.layue13.fakeitemcheckerreloaded.checker;

import com.google.common.base.Preconditions;
import com.layue13.fakeitemcheckerreloaded.dao.Repository;
import com.layue13.fakeitemcheckerreloaded.entity.Rule;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.function.Function;

public class FakedItemChecker {
    private final Repository<Rule,Long> ruleRepository;

    public FakedItemChecker(Repository<Rule,Long> ruleRepository) {
        Preconditions.checkNotNull(ruleRepository);
        this.ruleRepository = ruleRepository;
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

    public void check(Player holder, ItemStack[] itemStacks, Function<Player, Void> doAfterIfError) {
        Arrays.stream(itemStacks).forEach(itemStack -> {
            check(holder,itemStack,doAfterIfError);
        });
    }
}
