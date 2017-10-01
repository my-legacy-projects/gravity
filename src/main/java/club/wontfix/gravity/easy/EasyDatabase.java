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
                        resultSet.getBoolean("dev")
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
                        resultSet.getBoolean("dev")
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
                        resultSet.getBoolean("dev")
                );
            }
        }

        return User.NULL;
    }

    @SneakyThrows(SQLException.class)
    public boolean doesUserExist(@NonNull User user) {
        return isVerifyIDBound(user.getVerifyID()) && isUniqueIDBound(user.getUniqueID()) && isNameBound(user.getName());
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
