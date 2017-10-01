package club.wontfix.gravity.database;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface Database {

    Codec SQL_CODEC = new MySQLCodec(MySQLCodec.Mode.STANDARD);

    void connect();
    void disconnect();
    boolean isConnected();

    void setup() throws SQLException;
    void update(String query);
    ResultSet query(String query);

    Connection getConnection() throws SQLException;

    // Encodes a parameter to be safely passed with MySQL queries (prevents SQL Injection)
    default String encode(String parameter) {
        return ESAPI.encoder().encodeForSQL(SQL_CODEC, parameter);
    }

}
