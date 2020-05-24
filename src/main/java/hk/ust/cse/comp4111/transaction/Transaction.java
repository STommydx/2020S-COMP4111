package hk.ust.cse.comp4111.transaction;

import hk.ust.cse.comp4111.database.ConnectionManager;
import hk.ust.cse.comp4111.exception.BadTransactionActionException;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

public class Transaction implements AutoCloseable {
    private static int nextAvailableId = 0;
    private final int id;
    private final ConnectionManager.ConnectionInstance connection;

    public Transaction() throws SQLException, InterruptedException {
        synchronized (Transaction.class) {
            id = nextAvailableId++;
        }
        connection = ConnectionManager.getInstance().getConnectionInstance();
        connection.getConnection().setAutoCommit(false);
    }

    public int getId() {
        return id;
    }

    public ConnectionManager.ConnectionInstance getConnection() {
        return connection;
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }

    public static class TransactionAction {
        private final int bookId;
        private final boolean available;

        public TransactionAction(int bookId, boolean available) {
            this.bookId = bookId;
            this.available = available;
        }

        public TransactionAction(@NotNull TransactionActionRequest request) throws BadTransactionActionException {
            this.bookId = request.getBookId();
            if (request.getAction().equals("loan")) {
                this.available = false;
            } else if (request.getAction().equals("return")) {
                this.available = true;
            } else {
                throw new BadTransactionActionException(request.getAction());
            }
        }

        public int getBookId() {
            return bookId;
        }

        public boolean isAvailable() {
            return available;
        }

    }
}
