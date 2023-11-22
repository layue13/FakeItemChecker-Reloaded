package com.layue13.fakeitemcheckerreloaded.checker;

import com.google.common.base.Preconditions;
import com.layue13.fakeitemcheckerreloaded.FakeItemCheckerReloaded;
import com.layue13.fakeitemcheckerreloaded.ban.BanInfo;
import com.layue13.fakeitemcheckerreloaded.ban.BanMethod;
import com.layue13.fakeitemcheckerreloaded.dao.Repository;
import com.layue13.fakeitemcheckerreloaded.entity.Log;
import com.layue13.fakeitemcheckerreloaded.entity.Rule;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

public class FakedItemChecker {
    private final Repository<Rule, Long> ruleRepository;

    public FakedItemChecker(Repository<Rule, Long> ruleRepository) {
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
            check(holder, itemStack, doAfterIfError);
        });
    }

    public void checkInventory(FakeItemCheckerReloaded plugin, Event event, Player player, Inventory inventory, Collection<String> ignoredTitles) {
        if (ignoredTitles.contains(inventory.getTitle())) {
            return;
        }
        ItemStack[] inventoryItemStacks = inventory.getContents().clone();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            check(player, inventoryItemStacks, p -> {
                Log log = Log.builder()
                        .id(UUID.randomUUID())
                        .playerName(player.getName())
                        .time(new Date())
                        .server(Bukkit.getServer().getServerId())
                        .location(player.getLocation().toString())
                        .event(event.getEventName())
                        .inventoryType(inventory.getType())
                        .build();
                plugin.getLogRepository().save(log);
                plugin.getLogger().info(log.toString());
                Bukkit.getScheduler().runTask(plugin, () -> {
                    BanMethod.BUKKIT_BAN.ban(BanInfo.builder()
                            .player(player)
                            .reason(log.toString())
                            .server(plugin.getServer().getServerName())
                            .source(plugin.getName())
                            .build());
                    player.kickPlayer("You got banned.");
                });
                return null;
            });
        });
    }
}
