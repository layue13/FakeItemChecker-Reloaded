package com.layue13.fakeitemcheckerreloaded.dao;

import java.sql.Connection;

public abstract class SimpleMysqlRepository<T, ID> implements Repository<T, ID> {
    protected Connection connection;

    public SimpleMysqlRepository(Connection connection) {
        this.connection = connection;
    }

}
