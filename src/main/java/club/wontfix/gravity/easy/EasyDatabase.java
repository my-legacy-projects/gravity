package club.wontfix.gravity.easy;

import club.wontfix.gravity.Gravity;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EasyDatabase {

    @SneakyThrows(SQLException.class)
    public User getUserFromUniqueID(@NonNull String uniqueID) {
        uniqueID = Gravity.getInstance().getDatabase().encode(uniqueID);

        if(isUniqueIDBound(uniqueID)) {
            ResultSet resultSet = Gravity.getInstance().getDatabase().query(
                    "SELECT * FROM `gravity` WHERE uniqueID = '" + uniqueID + "'"
            );

            while (resultSet.next()) {
                return User.create(
                        resultSet.getString("name"),
                        resultSet.getString("uniqueID"),
                        resultSet.getString("verifyID"),
                        resultSet.getString("machineName"),
                        resultSet.getBoolean("dev"),
                        resultSet.getBoolean("killSwitched")
                );
            }
        }

        return User.NULL;
    }

    @SneakyThrows(SQLException.class)
    public User getUserFromVerifyID(@NonNull String verifyID) {
        verifyID = Gravity.getInstance().getDatabase().encode(verifyID);

        if(isVerifyIDBound(verifyID)) {
            ResultSet resultSet = Gravity.getInstance().getDatabase().query(
                    "SELECT * FROM `gravity` WHERE verifyID = '" + verifyID + "'"
            );

            while (resultSet.next()) {
                return User.create(
                        resultSet.getString("name"),
                        resultSet.getString("uniqueID"),
                        resultSet.getString("verifyID"),
                        resultSet.getString("machineName"),
                        resultSet.getBoolean("dev"),
                        resultSet.getBoolean("killSwitched")
                );
            }
        }

        return User.NULL;
    }

    @SneakyThrows(SQLException.class)
    public User getUserFromName(@NonNull String name) {
        name = Gravity.getInstance().getDatabase().encode(name);

        if(isNameBound(name)) {
            ResultSet resultSet = Gravity.getInstance().getDatabase().query(
                    "SELECT * FROM `gravity` WHERE name = '" + name + "'"
            );

            while (resultSet.next()) {
                return User.create(
                        resultSet.getString("name"),
                        resultSet.getString("uniqueID"),
                        resultSet.getString("verifyID"),
                        resultSet.getString("machineName"),
                        resultSet.getBoolean("dev"),
                        resultSet.getBoolean("killSwitched")
                );
            }
        }

        return User.NULL;
    }

    @SneakyThrows(SQLException.class)
    @SuppressWarnings("UnusedReturnValue")
    public User registerUser(@NonNull User user) {
        if (!doesUserExist(user)) {
            Gravity.getInstance().getDatabase().update(
                    "INSERT into `gravity` (name, uniqueID, verifyID, machineName, dev, killSwitched) VALUES " +
                            "('" + user.getName() + "', '" + user.getUniqueID() + "', " + user.getVerifyID() + "', " +
                            "'" + user.getMachineName() + "', " + user.isDev() + ", " + user.isKillSwitched() + ");"
            );

            return getUserFromUniqueID(user.getUniqueID());
        }

        return User.NULL;
    }

    @SneakyThrows(SQLException.class)
    public User registerUserUsingWaitingVerifyID(@NonNull String verifyID, @NonNull User user) {
        if (isVerifyIDWaiting(verifyID)) {
            ResultSet resultSet = Gravity.getInstance().getDatabase().query(
                    "SELECT * FROM `queue` WHERE verifyID = '" + verifyID + "'"
            );

            while (resultSet.next()) {
                String sqlName = resultSet.getString("name");
                String sqlVerifyID = resultSet.getString("verifyID");
                boolean sqlDev = resultSet.getBoolean("dev");

                if (sqlVerifyID.equals(verifyID)) {
                    Gravity.getInstance().getDatabase().update("DELETE FROM `queue` WHERE verifyID = '" + verifyID + "'");

                    user.setName(sqlName);
                    user.setVerifyID(sqlVerifyID);
                    user.setDev(sqlDev);

                    registerUser(user);
                }
            }
        }

        return User.NULL;
    }

    @SneakyThrows(SQLException.class)
    public void registerWaitingVerifyID(@NonNull String name, @NonNull String verifyID, boolean dev) {
        if (getUserFromVerifyID(verifyID) != User.NULL) {
            Gravity.getInstance().getDatabase().update(
                    "INSERT INTO `queue` (name, verifyID, dev) VALUES ('" + name + "', '" + verifyID + "', " + dev + ");"
            );
        }
    }

    public boolean doesUserExist(@NonNull User user) {
        if (user.getVerifyID() != null) {
            return isVerifyIDBound(user.getVerifyID());
        }
        if (user.getUniqueID() != null) {
            return isUniqueIDBound(user.getUniqueID());
        }

        return user.getName() != null && isNameBound(user.getName());
    }

    @SneakyThrows(SQLException.class)
    public boolean isVerifyIDWaiting(@NonNull String verifyID) {
        verifyID = Gravity.getInstance().getDatabase().encode(verifyID);

        ResultSet resultSet = Gravity.getInstance().getDatabase().query(
                "SELECT * FROM `queue` WHERE verifyID = '" + verifyID + "'"
        );

        return resultSet.next();
    }

    @SneakyThrows(SQLException.class)
    public boolean isVerifyIDBound(@NonNull String verifyID) {
        verifyID = Gravity.getInstance().getDatabase().encode(verifyID);

        ResultSet resultSet = Gravity.getInstance().getDatabase().query(
                "SELECT * FROM `gravity` WHERE verifyID = '" + verifyID + "'"
        );
        return resultSet.next();
    }

    @SneakyThrows(SQLException.class)
    public boolean isUniqueIDBound(@NonNull String uniqueID) {
        uniqueID = Gravity.getInstance().getDatabase().encode(uniqueID);

        ResultSet resultSet = Gravity.getInstance().getDatabase().query(
                "SELECT * FROM `gravity` WHERE uniqueID = '" + uniqueID + "'"
        );
        return resultSet.next();
    }

    @SneakyThrows(SQLException.class)
    public boolean isNameBound(@NonNull String name) {
        name = Gravity.getInstance().getDatabase().encode(name);

        ResultSet resultSet = Gravity.getInstance().getDatabase().query(
                "SELECT * FROM `gravity` WHERE name = '" + name + "'"
        );
        return resultSet.next();
    }

}
