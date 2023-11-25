package com.layue13.fakeitemcheckerreloaded.dao;

import com.layue13.fakeitemcheckerreloaded.entity.Rule;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class CachedRuleRepository extends RuleRepository {

    private final Date freshDataTime = new Date();
    private Collection<Rule> cachedRules = new CopyOnWriteArrayList<>();

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
        Collection<Rule> rulesInDatabase = super.getAll();
        cachedRules.addAll(rulesInDatabase);
        this.cachedRules = cachedRules.stream()
                .distinct()
                .collect(Collectors.toList());
    }
}
