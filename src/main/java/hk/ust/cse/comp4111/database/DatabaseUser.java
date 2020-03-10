package hk.ust.cse.comp4111.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUser {
    public static boolean authenticate(Connection connection, String username, String password) throws SQLException {
        boolean result = false;
        try (PreparedStatement statement = connection.prepareStatement("SELECT username FROM users WHERE username = ? AND password = ?")) {
            statement.setString(1, username);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) result = true;
            }
        }
        return result;
    }
}
