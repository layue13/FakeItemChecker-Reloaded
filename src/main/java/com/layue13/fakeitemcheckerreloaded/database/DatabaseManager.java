package com.layue13.fakeitemcheckerreloaded.database;

import com.layue13.fakeitemcheckerreloaded.config.PluginConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

@Slf4j
public class DatabaseManager {
    private final HikariDataSource dataSource;
    private final Logger logger;
    
    public DatabaseManager(PluginConfig.DatabaseConfig dbConfig, Logger logger) {
        this.logger = logger;
        
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbConfig.getUrl());
        config.setUsername(dbConfig.getUsername());
        config.setPassword(dbConfig.getPassword());
        config.setMaximumPoolSize(dbConfig.getMaxPoolSize());
        config.setMinimumIdle(dbConfig.getMinIdle());
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setLeakDetectionThreshold(60000);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        
        this.dataSource = new HikariDataSource(config);
        
        // Test connection
        try (Connection connection = dataSource.getConnection()) {
            logger.info("Database connection established successfully");
        } catch (SQLException e) {
            logger.severe("Failed to establish database connection: " + e.getMessage());
            throw new RuntimeException("Database connection failed", e);
        }
    }
    
    public DataSource getDataSource() {
        return dataSource;
    }
    
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Database connection pool closed");
        }
    }
    
    public boolean isHealthy() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(5);
        } catch (SQLException e) {
            log.error("Database health check failed", e);
            return false;
        }
    }
}