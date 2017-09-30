package club.wontfix.gravity.database;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "create")
public class MariaDatabase implements Database {

    @NonNull private final String address;
    @NonNull private final int port;
    @NonNull private final String database;
    @NonNull private final String username;
    @NonNull private final char[] password;

    @Override
    public void connect() {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public boolean isConnected() {
        return false;
    }
}
