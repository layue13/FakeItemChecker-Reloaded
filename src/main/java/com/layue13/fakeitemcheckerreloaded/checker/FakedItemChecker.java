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
import java.util.stream.Collectors;


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
            if (holder.hasPermission(rule.getPermission())) {
                return;
            }
            String[] split = rule.getItem().split(":");
            if (!itemStack.getType().name().equals(split[0])) {
                return;
            }
            if (!split[1].equals(String.valueOf(itemStack.getDurability()))) {
                return;
            }
            if (itemStack.getAmount() < Integer.parseInt(split[2])) {
                return;
            }
            doAfterIfError.accept(holder, rule);
        });
    }

    public void check(Player holder, ItemStack[] itemStacks, BiConsumer<Player, Rule> doAfterIfError) {
        Arrays.stream(itemStacks).filter(Objects::nonNull).filter(itemStack -> !itemStack.getType().equals(Material.AIR))
                .map(ItemStack::clone)
                .collect(Collectors.toMap(ItemStack::getType, itemStack -> itemStack, (o1, o2) -> {
                    o1.setAmount(o1.getAmount() + o2.getAmount());
                    return o1;
                })).values()
                .forEach(itemStack -> check(holder, itemStack, doAfterIfError));
    }
}
