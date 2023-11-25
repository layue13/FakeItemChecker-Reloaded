package com.layue13.fakeitemcheckerreloaded.dao;

import javax.sql.DataSource;

public abstract class DataSourceBasedRepository<T, ID> implements Repository<T, ID> {
    protected DataSource dataSource;

    public DataSourceBasedRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

}
