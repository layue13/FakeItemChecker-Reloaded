package com.layue13.fakeitemcheckerreloaded.dao;

import com.layue13.fakeitemcheckerreloaded.entity.Rule;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

public class CachedRuleRepository extends RuleRepository {

    private final Plugin plugin;
    private final Collection<Rule> cachedRules = new CopyOnWriteArrayList<>();

    private final Date freshDataTime = new Date();

    public CachedRuleRepository(Connection connection, Plugin plugin) {
        super(connection);
        this.plugin = plugin;
        fresh();
    }

    @Override
    public Collection<Rule> getAll() {
        if (cachedRules.isEmpty() || freshDataTime.before(new Date())) {
            plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {
                fresh();
                freshDataTime.setTime(System.currentTimeMillis() + 60 * 1000);
                plugin.getLogger().info("The following rules has been cached.");
                cachedRules.forEach(rule -> plugin.getLogger().info(rule.toString()));
            }, 1L);
        }
        return cachedRules;
    }

    public void fresh() {
        cachedRules.clear();
        cachedRules.addAll(super.getAll());
    }
}
