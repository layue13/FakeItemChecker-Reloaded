package com.layue13.fakeitemcheckerreloaded.dao;

import java.sql.Connection;

public abstract class AbstractBukkitRepository<T,ID> implements Dao<T,ID>{
    protected Connection connection;

    public AbstractBukkitRepository(Connection connection) {
        this.connection = connection;
    }

}
