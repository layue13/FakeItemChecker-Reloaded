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
            if (rule.getItemStack().getType().equals(itemStack.getType()) && rule.getItemStack().getDurability() == itemStack.getDurability() && rule.getItemStack().getAmount() <= itemStack.getAmount()) {
                if (!holder.hasPermission(rule.getPermission()) && !holder.isOp()) {
                    doAfterIfError.accept(holder, rule);
                }
            }
        });
    }

    public void check(Player holder, ItemStack[] itemStacks, BiConsumer<Player, Rule> doAfterIfError) {
        Arrays.stream(itemStacks.clone()).filter(Objects::nonNull).filter(itemStack -> !itemStack.getType().equals(Material.AIR)).map(ItemStack::clone).collect(Collectors.toMap(itemStack -> itemStack.getType().name() + ":" + String.valueOf(itemStack.getDurability()), itemStack -> itemStack, (o1, o2) -> {
            o1.setAmount(o1.getAmount() + o2.getAmount());
            return o1;
        })).values().forEach(itemStack -> check(holder, itemStack, doAfterIfError));
    }
}
