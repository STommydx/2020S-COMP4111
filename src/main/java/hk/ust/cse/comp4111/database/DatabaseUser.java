package hk.ust.cse.comp4111.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUser {
    public static boolean authenticate(Connection connection, String username, String password) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        boolean result = false;
        try {
            statement = connection.prepareStatement("SELECT username FROM users WHERE username = ? AND password = ?");
            statement.setString(1, username);
            statement.setString(2, password);
            resultSet = statement.executeQuery();
            if (resultSet.next()) result = true;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        }
        return result;
    }
}
