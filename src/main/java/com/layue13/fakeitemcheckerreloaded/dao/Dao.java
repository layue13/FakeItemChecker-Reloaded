package com.layue13.fakeitemcheckerreloaded.dao;

import java.util.Collection;
import java.util.Optional;

/**
 * Dao interface
 * @param <T>  represent loaded data type.
 * @param <ID> represent ID type.
 */

public interface Dao<T,ID> {
    void init();
    Optional<T> get(ID id);

    Collection<T> getAll();

    void save(T t);

    void update(T t, String[] params);

    void delete(T t);
}
