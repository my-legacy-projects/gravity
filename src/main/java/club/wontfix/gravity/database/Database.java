package club.wontfix.gravity.database;

public interface Database {

    void connect();
    void disconnect();
    boolean isConnected();


}
