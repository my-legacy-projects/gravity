package club.wontfix.gravity.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface Database {

    void connect();
    void disconnect();
    boolean isConnected();

    void setup() throws SQLException;
    void update(String query);
    ResultSet query(String query);

    Connection getConnection() throws SQLException;

}
