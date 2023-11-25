package com.layue13.fakeitemcheckerreloaded.dao;

import com.google.common.base.Preconditions;
import com.layue13.fakeitemcheckerreloaded.entity.Log;
import org.bukkit.event.inventory.InventoryType;

import javax.sql.DataSource;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class LogRepository extends DataSourceBasedRepository<Log, UUID> {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final RuleRepository ruleRepository = new RuleRepository(dataSource);

    public LogRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void init() {
        try (Connection connection = super.dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                String sql = "CREATE TABLE IF NOT EXISTS logs"
                        + "(id            VARCHAR(36) PRIMARY KEY,"
                        + "player_name    VARCHAR(16),"
                        + "server         VARCHAR(36),"
                        + "time           DATETIME,"
                        + "location       TEXT,"
                        + "event          TEXT,"
                        + "inventory_type TEXT,"
                        + "rule_id        INT REFERENCES rules(id)"
                        + ")";
                statement.execute(sql);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Log> get(UUID id) {
        try (Connection connection = super.dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM logs WHERE id=?")) {
                preparedStatement.setString(1, id.toString());
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (!resultSet.next()) return Optional.empty();
                    return Optional.of(assembleLogFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<Log> getAll() {
        Collection<Log> collection = new ArrayList<>();
        try (Connection connection = super.dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM logs")) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        collection.add(assembleLogFromResultSet(resultSet));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return collection;
    }

    @Override
    public void save(Log log) {
        Preconditions.checkNotNull(log);
        try (Connection connection = super.dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO logs(id,player_name,server,time,location,event,inventory_type,rule_id) VALUES (?,?,?,?,?,?,?,?)")) {
                preparedStatement.setString(1, log.getId().toString());
                preparedStatement.setString(2, log.getPlayerName());
                preparedStatement.setString(3, log.getServer());
                preparedStatement.setString(4, dateFormat.format(log.getTime()));
                preparedStatement.setString(5, log.getLocation());
                preparedStatement.setString(6, log.getEvent());
                preparedStatement.setString(7, log.getInventoryType().toString());
                preparedStatement.setLong(8, log.getRule().getId());
                preparedStatement.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Log log, String[] params) {

    }

    @Override
    public void delete(Log log) {
        try (Connection connection = super.dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM logs WHERE id=?", new String[]{String.valueOf(log.getId())})) {
                preparedStatement.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Log assembleLogFromResultSet(ResultSet resultSet) {
        try {
            return Log.builder()
                    .id(UUID.fromString(resultSet.getString("id")))
                    .server(resultSet.getString("server"))
                    .time(resultSet.getDate("time"))
                    .inventoryType(InventoryType.valueOf(resultSet.getString("inventory_type")))
                    .location(resultSet.getString("location"))
                    .event(resultSet.getString("event"))
                    .rule(ruleRepository.get(resultSet.getLong("rule_id")).orElseThrow((Supplier<Throwable>) () -> new SQLDataException("can't get rule id.")))
                    .build();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
