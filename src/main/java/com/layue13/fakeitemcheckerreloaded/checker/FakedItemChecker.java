package com.layue13.fakeitemcheckerreloaded.checker;

import com.google.common.base.Preconditions;
import com.layue13.fakeitemcheckerreloaded.dao.Repository;
import com.layue13.fakeitemcheckerreloaded.entity.Rule;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiConsumer;


public class FakedItemChecker {
    private final Repository<Rule, Long> ruleRepository;

    public FakedItemChecker(Repository<Rule, Long> ruleRepository) {
        Preconditions.checkNotNull(ruleRepository);
        this.ruleRepository = ruleRepository;
    }

    public void check(Player holder, ItemStack itemStack, BiConsumer<Player, Rule> doAfterIfError) {
        Preconditions.checkNotNull(holder);
        Preconditions.checkNotNull(itemStack);
        Preconditions.checkNotNull(doAfterIfError);
        ruleRepository.getAll().forEach(rule -> {
            if (!holder.hasPermission(rule.getPermission())) {
                String item = itemStack.getType().name() + ":" + itemStack.getDurability();
                if (rule.getItem().equals(item)) {
                    doAfterIfError.accept(holder, rule);
                }
            }
        });
    }

    public void check(Player holder, ItemStack[] itemStacks, BiConsumer<Player, Rule> doAfterIfError) {
        Arrays.stream(itemStacks)
                .filter(Objects::nonNull)
                .filter(itemStack -> !itemStack.getType().equals(Material.AIR))
                .forEach(itemStack -> check(holder, itemStack, doAfterIfError));
    }
}
