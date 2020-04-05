package hk.ust.cse.comp4111.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionManager {
    private static final String host = "localhost";
    private static String username = "comp4111";
    private static String password = "comp4111";

    public static Connection getConnection() throws SQLException {
        Connection connection;
        Properties connectionProperties = new Properties();
        connectionProperties.put("user", "comp4111");
        connectionProperties.put("password", "comp4111");
        connection = DriverManager.getConnection("jdbc:mysql://" + host + "/comp4111", connectionProperties);
        return connection;
    }
}
