package com.layue13.fakeitemcheckerreloaded.repository;

import com.layue13.fakeitemcheckerreloaded.entity.Rule;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Slf4j
public class RuleRepository implements Repository<Rule, Long> {
    
    private final DataSource dataSource;
    
    private static final String CREATE_TABLE_SQL = 
            "CREATE TABLE IF NOT EXISTS rules (" +
            "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
            "item VARCHAR(255) NOT NULL," +
            "permission VARCHAR(255) NOT NULL," +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
            "INDEX idx_item (item)," +
            "INDEX idx_permission (permission)" +
            ")";
    
    private static final String INSERT_SQL = "INSERT INTO rules (item, permission) VALUES (?, ?)";
    private static final String UPDATE_SQL = "UPDATE rules SET item = ?, permission = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM rules WHERE id = ?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM rules WHERE id = ?";
    private static final String FIND_ALL_SQL = "SELECT * FROM rules ORDER BY id";
    private static final String EXISTS_SQL = "SELECT COUNT(*) FROM rules WHERE id = ?";
    private static final String COUNT_SQL = "SELECT COUNT(*) FROM rules";
    
    public RuleRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Override
    public void init() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(CREATE_TABLE_SQL);
            log.info("Rules table initialized successfully");
        } catch (SQLException e) {
            log.error("Failed to initialize rules table", e);
            throw new RuntimeException("Failed to initialize rules table", e);
        }
    }
    
    @Override
    public Optional<Rule> findById(Long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(FIND_BY_ID_SQL)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRule(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Failed to find rule by id: " + id, e);
            throw new RuntimeException("Failed to find rule by id: " + id, e);
        }
        return Optional.empty();
    }
    
    @Override
    public Collection<Rule> findAll() {
        Collection<Rule> rules = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                rules.add(mapResultSetToRule(rs));
            }
        } catch (SQLException e) {
            log.error("Failed to find all rules", e);
            throw new RuntimeException("Failed to find all rules", e);
        }
        return rules;
    }
    
    @Override
    public Rule save(Rule rule) {
        if (!rule.isValid()) {
            String error = rule.getValidationError();
            throw new IllegalArgumentException("Invalid rule: " + error);
        }
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, rule.getItem());
            stmt.setString(2, rule.getPermission());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Failed to save rule");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    rule.setId(generatedKeys.getLong(1));
                } else {
                    throw new RuntimeException("Failed to get generated ID for rule");
                }
            }
            
            log.debug("Saved rule: {}", rule);
            return rule;
        } catch (SQLException e) {
            log.error("Failed to save rule: " + rule, e);
            throw new RuntimeException("Failed to save rule", e);
        }
    }
    
    @Override
    public Rule update(Rule rule) {
        if (rule.getId() == null) {
            throw new IllegalArgumentException("Rule ID cannot be null for update");
        }
        if (!rule.isValid()) {
            String error = rule.getValidationError();
            throw new IllegalArgumentException("Invalid rule: " + error);
        }
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, rule.getItem());
            stmt.setString(2, rule.getPermission());
            stmt.setLong(3, rule.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Failed to update rule with id: " + rule.getId());
            }
            
            log.debug("Updated rule: {}", rule);
            return rule;
        } catch (SQLException e) {
            log.error("Failed to update rule: " + rule, e);
            throw new RuntimeException("Failed to update rule", e);
        }
    }
    
    @Override
    public void delete(Rule rule) {
        if (rule.getId() == null) {
            throw new IllegalArgumentException("Rule ID cannot be null for deletion");
        }
        deleteById(rule.getId());
    }
    
    @Override
    public void deleteById(Long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(DELETE_SQL)) {
            stmt.setLong(1, id);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                log.warn("No rule found with id: {}", id);
            } else {
                log.debug("Deleted rule with id: {}", id);
            }
        } catch (SQLException e) {
            log.error("Failed to delete rule with id: " + id, e);
            throw new RuntimeException("Failed to delete rule with id: " + id, e);
        }
    }
    
    @Override
    public boolean exists(Long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(EXISTS_SQL)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getLong(1) > 0;
            }
        } catch (SQLException e) {
            log.error("Failed to check existence of rule with id: " + id, e);
            throw new RuntimeException("Failed to check existence of rule with id: " + id, e);
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
            log.error("Failed to count rules", e);
            throw new RuntimeException("Failed to count rules", e);
        }
        return 0;
    }
    
    private Rule mapResultSetToRule(ResultSet rs) throws SQLException {
        return Rule.builder()
                .id(rs.getLong("id"))
                .item(rs.getString("item"))
                .permission(rs.getString("permission"))
                .build();
    }
}