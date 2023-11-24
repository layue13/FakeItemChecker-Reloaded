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

    private Date freshDataTime = new Date();

    public CachedRuleRepository(Connection connection, Plugin plugin) {
        super(connection);
        this.plugin = plugin;
        fresh();
    }

    @Override
    public Collection<Rule> getAll() {
        fresh();
        return cachedRules;
    }

    public void fresh() {
        if (freshDataTime.before(new Date())) {
            plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {
                cachedRules.clear();
                cachedRules.addAll(super.getAll());
                freshDataTime = new Date(System.currentTimeMillis() + 100000);
                plugin.getLogger().info(CachedRuleRepository.class.getName() + " will be upgrading at " + freshDataTime.toString());
            }, 1L);
        }
    }
}
