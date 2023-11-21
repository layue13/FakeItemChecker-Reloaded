package com.layue13.fakeitemcheckerreloaded.dao;

import com.google.common.base.Preconditions;
import com.layue13.fakeitemcheckerreloaded.entity.Log;
import org.bukkit.event.inventory.InventoryType;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class LogRepository extends AbstractBukkitRepository<Log,UUID> {

    public LogRepository(Connection connection) {
        super(connection);
    }

    @Override
    public void init() {
        try (Statement statement = connection.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS logs"
                    + "(id            VARCHAR(36) PRIMARY KEY,"
                    + "player_name    VARCHAR(16),"
                    + "server         VARCHAR(36),"
                    + "time           DATETIME,"
                    + "location       TEXT,"
                    + "event          TEXT,"
                    + "inventory_type TEXT"
                    + ")";
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Log> get(UUID id) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM logs WHERE id=?")) {
            preparedStatement.setString(1, id.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return Optional.of(assembleLogFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<Log> getAll() {
        Collection<Log> collection = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM logs")) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                do {
                    collection.add(assembleLogFromResultSet(resultSet));
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return collection;
    }

    @Override
    public void save(Log log) {
        Preconditions.checkNotNull(log);
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO logs(id,player_name,server,time,location,event,inventory_type) VALUES (?,?,?,?,?,?,?)")) {
            preparedStatement.setString(1, log.getId().toString());
            preparedStatement.setString(2, log.getPlayerName());
            preparedStatement.setString(3, log.getServer());
            preparedStatement.setTime(4, (Time) log.getTime());
            preparedStatement.setString(5, log.getLocation());
            preparedStatement.setString(6, log.getEvent());
            preparedStatement.setString(7, log.getInventoryType().toString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Log log, String[] params) {

    }

    @Override
    public void delete(Log log) {
        try {
            connection.prepareStatement("DELETE * FROM logs WHERE id=?", new String[]{String.valueOf(log.getId())}).execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Log assembleLogFromResultSet(ResultSet resultSet) {
        try {
            return Log.builder().id(UUID.fromString(resultSet.getString("id"))).server(resultSet.getString("server")).time(resultSet.getTime("time")).inventoryType(InventoryType.valueOf(resultSet.getString("inventory_type"))).location(resultSet.getString("location")).event(resultSet.getString("event")).build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
