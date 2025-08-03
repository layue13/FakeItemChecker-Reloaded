package com.layue13.fakeitemcheckerreloaded.repository;

import com.layue13.fakeitemcheckerreloaded.entity.Log;
import com.layue13.fakeitemcheckerreloaded.entity.Rule;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Slf4j
public class LogRepository implements Repository<Log, String> {
    
    private final DataSource dataSource;
    
    private static final String CREATE_TABLE_SQL = 
            "CREATE TABLE IF NOT EXISTS logs (" +
            "id VARCHAR(36) PRIMARY KEY," +
            "player_name VARCHAR(255) NOT NULL," +
            "time TIMESTAMP NOT NULL," +
            "server VARCHAR(255) NOT NULL," +
            "location TEXT," +
            "event VARCHAR(255)," +
            "inventory_type VARCHAR(255)," +
            "rule_id BIGINT," +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "INDEX idx_player_name (player_name)," +
            "INDEX idx_time (time)," +
            "INDEX idx_rule_id (rule_id)," +
            "FOREIGN KEY (rule_id) REFERENCES rules(id) ON DELETE SET NULL" +
            ")";
    
    private static final String INSERT_SQL = 
            "INSERT INTO logs (id, player_name, time, server, location, event, inventory_type, rule_id) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String UPDATE_SQL = 
            "UPDATE logs SET player_name = ?, time = ?, server = ?, location = ?, event = ?, " +
            "inventory_type = ?, rule_id = ? WHERE id = ?";
    
    private static final String DELETE_SQL = "DELETE FROM logs WHERE id = ?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM logs WHERE id = ?";
    private static final String FIND_ALL_SQL = "SELECT * FROM logs ORDER BY time DESC";
    private static final String EXISTS_SQL = "SELECT COUNT(*) FROM logs WHERE id = ?";
    private static final String COUNT_SQL = "SELECT COUNT(*) FROM logs";
    
    public LogRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Override
    public void init() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(CREATE_TABLE_SQL);
            log.info("Logs table initialized successfully");
        } catch (SQLException e) {
            log.error("Failed to initialize logs table", e);
            throw new RuntimeException("Failed to initialize logs table", e);
        }
    }
    
    @Override
    public Optional<Log> findById(String id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(FIND_BY_ID_SQL)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToLog(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Failed to find log by id: " + id, e);
            throw new RuntimeException("Failed to find log by id: " + id, e);
        }
        return Optional.empty();
    }
    
    @Override
    public Collection<Log> findAll() {
        Collection<Log> logs = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                logs.add(mapResultSetToLog(rs));
            }
        } catch (SQLException e) {
            log.error("Failed to find all logs", e);
            throw new RuntimeException("Failed to find all logs", e);
        }
        return logs;
    }
    
    @Override
    public Log save(Log logEntry) {
        if (logEntry.getId() == null || logEntry.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Log ID cannot be null or empty");
        }
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(INSERT_SQL)) {
            stmt.setString(1, logEntry.getId());
            stmt.setString(2, logEntry.getPlayerName());
            stmt.setTimestamp(3, new Timestamp(logEntry.getTime().getTime()));
            stmt.setString(4, logEntry.getServer());
            stmt.setString(5, logEntry.getLocation());
            stmt.setString(6, logEntry.getEvent());
            stmt.setString(7, logEntry.getInventoryType());
            stmt.setLong(8, logEntry.getRule().getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Failed to save log");
            }
            
            log.debug("Saved log: {}", logEntry);
            return logEntry;
        } catch (SQLException e) {
            log.error("Failed to save log: " + logEntry, e);
            throw new RuntimeException("Failed to save log", e);
        }
    }
    
    @Override
    public Log update(Log logEntry) {
        if (logEntry.getId() == null || logEntry.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Log ID cannot be null or empty for update");
        }
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, logEntry.getPlayerName());
            stmt.setTimestamp(2, new Timestamp(logEntry.getTime().getTime()));
            stmt.setString(3, logEntry.getServer());
            stmt.setString(4, logEntry.getLocation());
            stmt.setString(5, logEntry.getEvent());
            stmt.setString(6, logEntry.getInventoryType());
            stmt.setLong(7, logEntry.getRule().getId());
            stmt.setString(8, logEntry.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Failed to update log with id: " + logEntry.getId());
            }
            
            log.debug("Updated log: {}", logEntry);
            return logEntry;
        } catch (SQLException e) {
            log.error("Failed to update log: " + logEntry, e);
            throw new RuntimeException("Failed to update log", e);
        }
    }
    
    @Override
    public void delete(Log logEntry) {
        if (logEntry.getId() == null || logEntry.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Log ID cannot be null or empty for deletion");
        }
        deleteById(logEntry.getId());
    }
    
    @Override
    public void deleteById(String id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(DELETE_SQL)) {
            stmt.setString(1, id);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                log.warn("No log found with id: {}", id);
            } else {
                log.debug("Deleted log with id: {}", id);
            }
        } catch (SQLException e) {
            log.error("Failed to delete log with id: " + id, e);
            throw new RuntimeException("Failed to delete log with id: " + id, e);
        }
    }
    
    @Override
    public boolean exists(String id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(EXISTS_SQL)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getLong(1) > 0;
            }
        } catch (SQLException e) {
            log.error("Failed to check existence of log with id: " + id, e);
            throw new RuntimeException("Failed to check existence of log with id: " + id, e);
        }
    }
    
    @Override
    public long count() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(COUNT_SQL);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            log.error("Failed to count logs", e);
            throw new RuntimeException("Failed to count logs", e);
        }
        return 0;
    }
    
    private Log mapResultSetToLog(ResultSet rs) throws SQLException {
        Rule rule = null;
        Long ruleId = rs.getLong("rule_id");
        if (!rs.wasNull()) {
            // In a real implementation, you might want to fetch the rule from the repository
            // For now, we'll create a minimal rule object
            rule = Rule.builder().id(ruleId).build();
        }
        
        return Log.builder()
                .id(rs.getString("id"))
                .playerName(rs.getString("player_name"))
                .time(rs.getTimestamp("time"))
                .server(rs.getString("server"))
                .location(rs.getString("location"))
                .event(rs.getString("event"))
                .inventoryType(rs.getString("inventory_type"))
                .rule(rule)
                .build();
    }
}