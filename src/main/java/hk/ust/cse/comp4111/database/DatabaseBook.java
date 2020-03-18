package hk.ust.cse.comp4111.database;

import hk.ust.cse.comp4111.exception.BookNotExistException;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseBook {

    public static int bookExist(Connection connection, String title, String author, String publisher, int year) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT id FROM books WHERE title = ? AND author = ? AND publisher = ? AND year = ?")) {
            statement.setString(1, title);
            statement.setString(2, author);
            statement.setString(3, publisher);
            statement.setInt(4, year);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }
        return -1;
    }


    public static boolean curAvailability(Connection connection, int id) throws SQLException, BookNotExistException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT id, available FROM books WHERE id = ? FOR UPDATE")) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(2);
            } else {
                throw new BookNotExistException();
            }
        }

    }


    public static boolean addBookRecord(Connection connection, String title, String author, String publisher, int year) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO books (title, author, publisher,year) VALUES (?,?,?,?)")) {
            statement.setString(1, title);
            statement.setString(2, author);
            statement.setString(3, publisher);
            statement.setInt(4, year);

            statement.execute();

            return false;
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")) {
                // duplicate book found, violates integrity constraint
                return true;
            }
            throw e;
        }
    }

    public static void updateBookAvailability(@NotNull Connection connection, int id, boolean available) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE books SET available = ? WHERE id = ?");
        statement.setBoolean(1, available);
        statement.setInt(2, id);
        statement.execute();
        statement.close();
    }

    public static boolean deleteBook(@NotNull Connection connection, int id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM books WHERE id = ?")) {
            statement.setInt(1, id);
            int count = statement.executeUpdate();
            return count > 0;
        }
    }
}
