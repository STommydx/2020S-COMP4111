package hk.ust.cse.comp4111.database;

import hk.ust.cse.comp4111.transaction.Transaction;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DatabaseTransaction {
    public static boolean commit(@NotNull Connection connection, @NotNull List<Transaction.TransactionAction> actions) throws SQLException {
        connection.setAutoCommit(false);
        try (PreparedStatement preparedSelection = connection.prepareStatement("SELECT available FROM books WHERE id = ? FOR UPDATE")) {
            try (PreparedStatement preparedUpdate = connection.prepareStatement("UPDATE books SET available = ? WHERE id = ?")) {
                for (Transaction.TransactionAction action : actions) {
                    preparedSelection.setInt(1, action.getBookId());
                    try (ResultSet resultSet = preparedSelection.executeQuery()) {
                        if (!resultSet.next()) {
                            // book not found
                            connection.rollback();
                            return false;
                        }
                        int isAvailable = resultSet.getInt(1);
                        if (isAvailable == (action.isAvailable() ? 1 : 0)) {
                            connection.rollback();
                            return false;
                        }
                    }
                    preparedUpdate.setInt(1, action.isAvailable() ? 1 : 0);
                    preparedUpdate.setInt(2, action.getBookId());
                    preparedUpdate.executeUpdate();
                }
            }
        }
        connection.commit();
        return true;
    }
}
