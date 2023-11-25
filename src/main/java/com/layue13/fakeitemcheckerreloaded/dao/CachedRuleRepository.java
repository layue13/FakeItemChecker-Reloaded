package com.layue13.fakeitemcheckerreloaded.dao;

import com.layue13.fakeitemcheckerreloaded.entity.Rule;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Date;

public class CachedRuleRepository extends RuleRepository {

    private final Date freshDataTime = new Date();
    private Collection<Rule> cachedRules;

    public CachedRuleRepository(DataSource dataSource) {
        super(dataSource);
        fresh();
    }

    @Override
    public synchronized Collection<Rule> getAll() {
        if (cachedRules == null || cachedRules.isEmpty() || freshDataTime.before(new Date())) {
            fresh();
            freshDataTime.setTime(System.currentTimeMillis() + 120 * 1000);
        }
        return cachedRules;
    }

    public void fresh() {
        cachedRules = null;
        this.cachedRules = super.getAll();
    }
}
