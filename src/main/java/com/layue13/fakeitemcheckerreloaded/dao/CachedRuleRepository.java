package com.layue13.fakeitemcheckerreloaded.dao;

import com.layue13.fakeitemcheckerreloaded.entity.Rule;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CachedRuleRepository extends RuleRepository {

    private final Plugin plugin;
    private final Lock lock = new ReentrantLock();
    private final Date freshDataTime = new Date();
    private Collection<Rule> cachedRules;

    public CachedRuleRepository(Connection connection, Plugin plugin) {
        super(connection);
        this.plugin = plugin;
        fresh();
    }

    @Override
    public Collection<Rule> getAll() {
        if (cachedRules == null || cachedRules.isEmpty() || freshDataTime.before(new Date())) {
            plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                lock.lock();
                if (cachedRules == null || cachedRules.isEmpty() || freshDataTime.before(new Date())) {
                    fresh();
                    freshDataTime.setTime(System.currentTimeMillis() + 120 * 1000);
                }
                lock.unlock();
            });
        }
        return cachedRules;
    }

    public void fresh() {
        cachedRules = null;
        this.cachedRules = super.getAll();
    }
}
