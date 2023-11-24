package com.layue13.fakeitemcheckerreloaded.dao;

import com.google.common.base.Preconditions;
import com.layue13.fakeitemcheckerreloaded.entity.Rule;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class RuleRepository extends SimpleMysqlRepository<Rule, Long> {
    public RuleRepository(Connection connection) {
        super(connection);
    }

    @Override
    public void init() {
        try (Statement statement = this.connection.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS rules"
                    + "(id              INT PRIMARY KEY AUTO_INCREMENT,"
                    + "item             TEXT,"
                    + "permission       TEXT"
                    + ")";
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Rule> get(Long id) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM rules WHERE id=?")) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) return Optional.empty();
                return Optional.of(assembleRuleFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<Rule> getAll() {
        Collection<Rule> collection = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM rules")) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    collection.add(assembleRuleFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return collection;
    }

    @Override
    public void save(Rule rule) {
        Preconditions.checkNotNull(rule);
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO rules(item,permission) VALUES (?,?)")) {
            preparedStatement.setString(1, rule.getItem());
            preparedStatement.setString(2, rule.getPermission());
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Rule rule, String[] params) {

    }

    @Override
    public void delete(Rule rule) {
        Preconditions.checkNotNull(rule);
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM logs WHERE id=?")) {
            preparedStatement.setLong(1, rule.getId());
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Rule assembleRuleFromResultSet(ResultSet resultSet) {
        try {
            return Rule.builder()
                    .id(resultSet.getLong("id"))
                    .item(resultSet.getString("item"))
                    .permission(resultSet.getString("permission"))
                    .build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
