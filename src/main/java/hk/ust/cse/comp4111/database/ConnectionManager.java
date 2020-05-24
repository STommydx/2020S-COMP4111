package hk.ust.cse.comp4111.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.Semaphore;

public class ConnectionManager {
    private static final String HOST = "localhost";
    private static final String USERNAME = "comp4111";
    private static final String PASSWORD = "comp4111";
    private static final String DATABASE_NAME = "comp4111";
    private static final int MAX_CONNECTION = 144;

    private static final ConnectionManager instance = new ConnectionManager();
    private final Semaphore sem;

    private ConnectionManager() {
        sem = new Semaphore(MAX_CONNECTION);
    }

    public static ConnectionManager getInstance() {
        return instance;
    }

    public ConnectionInstance getConnectionInstance() throws SQLException, InterruptedException {
        sem.acquire();
        return new ConnectionInstance();
    }

    public class ConnectionInstance implements AutoCloseable {
        private final Connection connection;

        public ConnectionInstance() throws SQLException {
            Properties connectionProperties = new Properties();
            connectionProperties.put("user", USERNAME);
            connectionProperties.put("password", PASSWORD);
            connection = DriverManager.getConnection("jdbc:mysql://" + HOST + "/" + DATABASE_NAME, connectionProperties);
        }

        public Connection getConnection() {
            return connection;
        }

        @Override
        public void close() throws SQLException {
            connection.close();
            sem.release();
        }
    }

}
