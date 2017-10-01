package club.wontfix.gravity.database.impl;

import club.wontfix.gravity.Gravity;
import club.wontfix.gravity.database.Database;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

@RequiredArgsConstructor(staticName = "create")
public class MariaDatabase implements Database {

    @Getter @Setter
    private HikariDataSource dataSource;

    @NonNull
    private final String address;

    @NonNull
    private final int port;

    @NonNull
    private final String database;

    @NonNull
    private final String username;

    @NonNull
    private final char[] password;

    @Override
    public void connect() {
        if(!isConnected()) {
            HikariConfig config = new HikariConfig();
            config.setDriverClassName("org.mariadb.jdbc.Driver");
            config.setJdbcUrl("jdbc:mariadb://" + address + ":" + port + "/" + database);
            config.setUsername(username);
            config.setPassword(new String(password));
            config.setMinimumIdle(5);
            config.setMaximumPoolSize(20);
            config.setConnectionTimeout(5000);
            setDataSource(new HikariDataSource(config));
        }
    }

    @Override
    public void disconnect() {
        if(isConnected()) {
            dataSource.close();
        }
    }

    @Override
    public boolean isConnected() {
        return (dataSource != null) && (!dataSource.isClosed());
    }

    @Override
    public void setup() throws SQLException {
        update("CREATE TABLE IF NOT EXISTS `gravity` (" +
                "name VARCHAR(64), uniqueID VARCHAR(64), verifyID VARCHAR(64), machineName VARCHAR(64), dev BOOLEAN" +
                ");");
    }

    @Override
    public void update(String query) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            statement = (connection = getConnection()).prepareStatement(query);
            statement.executeUpdate();
        } catch (SQLException ex) {
            Gravity.getInstance().getLogger().error("Error while executing SQL Update!", ex);
        } finally {
            close(statement, connection, null);
        }
    }

    @Override
    public ResultSet query(String query) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = (connection = getConnection()).prepareStatement(query);
            resultSet = statement.executeQuery();
        } catch (SQLException ex) {
            Gravity.getInstance().getLogger().error("Error while executing SQL Query!", ex);
        } finally {
            close(statement, connection, resultSet);
        }

        return resultSet;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if(isConnected()) {
            return dataSource.getConnection();
        }

        return null;
    }

    private void close(Statement statement, Connection connection, ResultSet resultSet) {
        if(statement != null) {
            try {
                statement.close();
            } catch (SQLException ex) {
                Gravity.getInstance().getLogger().error("Error while closing Statement!", ex);
            }
        }
        if(connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                Gravity.getInstance().getLogger().error("Error while closing Connection!", ex);
            }
        }
        if(resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException ex) {
                Gravity.getInstance().getLogger().error("Error while closing ResultSet!", ex);
            }
        }
    }

}
