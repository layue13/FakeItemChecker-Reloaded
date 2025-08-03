package com.layue13.fakeitemcheckerreloaded.repository;

import java.util.Collection;
import java.util.Optional;

/**
 * Generic repository interface for data access operations
 * @param <T> Entity type
 * @param <ID> ID type
 */
public interface Repository<T, ID> {
    
    /**
     * Initialize the repository (create tables, etc.)
     */
    void init();
    
    /**
     * Find entity by ID
     * @param id Entity ID
     * @return Optional containing the entity if found
     */
    Optional<T> findById(ID id);
    
    /**
     * Find all entities
     * @return Collection of all entities
     */
    Collection<T> findAll();
    
    /**
     * Save entity
     * @param entity Entity to save
     * @return Saved entity with generated ID
     */
    T save(T entity);
    
    /**
     * Update entity
     * @param entity Entity to update
     * @return Updated entity
     */
    T update(T entity);
    
    /**
     * Delete entity
     * @param entity Entity to delete
     */
    void delete(T entity);
    
    /**
     * Delete entity by ID
     * @param id Entity ID
     */
    void deleteById(ID id);
    
    /**
     * Check if entity exists
     * @param id Entity ID
     * @return true if entity exists
     */
    boolean exists(ID id);
    
    /**
     * Count total entities
     * @return Total count
     */
    long count();
}