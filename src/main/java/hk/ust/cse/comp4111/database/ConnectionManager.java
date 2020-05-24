package hk.ust.cse.comp4111.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionManager {
    private static final String HOST = "localhost";
    private static final String USERNAME = "comp4111";
    private static final String PASSWORD = "comp4111";
    private static final String DATABASE_NAME = "comp4111";

    public static Connection getConnection() throws SQLException {
        Properties connectionProperties = new Properties();
        connectionProperties.put("user", USERNAME);
        connectionProperties.put("password", PASSWORD);
        return DriverManager.getConnection("jdbc:mysql://" + HOST + "/" + DATABASE_NAME, connectionProperties);
    }
}
