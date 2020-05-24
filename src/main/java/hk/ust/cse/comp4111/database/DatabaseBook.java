package hk.ust.cse.comp4111.database;

import hk.ust.cse.comp4111.book.AddBookRequest;
import hk.ust.cse.comp4111.book.BookSearchRequest;
import hk.ust.cse.comp4111.book.BookSearchResponse;
import hk.ust.cse.comp4111.exception.BookNotExistException;
import hk.ust.cse.comp4111.exception.LockWaitTimeoutException;
import org.jetbrains.annotations.NotNull;

import java.sql.*;

public class DatabaseBook {

    public static int TIMEOUT_VALUE = 5;

    public static int isBookExist(Connection connection, String title, String author, String publisher, int year) throws SQLException {
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


    public static boolean isBookCurrentlyAvailable(Connection connection, int id) throws SQLException, BookNotExistException, LockWaitTimeoutException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT id, available FROM books WHERE id = ? FOR UPDATE")) {
            statement.setInt(1, id);
            statement.setQueryTimeout(TIMEOUT_VALUE);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(2);
            } else {
                throw new BookNotExistException();
            }
        } catch (SQLTimeoutException e) {
            throw new LockWaitTimeoutException();
        }
    }


    public static boolean addBookRecord(Connection connection, String title, String author, String publisher, int year) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO books (title, author, publisher, year) VALUES (?,?,?,?)")) {
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
        try (PreparedStatement statement = connection.prepareStatement("UPDATE books SET available = ? WHERE id = ?")) {
            statement.setBoolean(1, available);
            statement.setInt(2, id);
            statement.execute();
        }
    }

    public static boolean deleteBook(@NotNull Connection connection, int id) throws SQLException, LockWaitTimeoutException {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM books WHERE id = ?")) {
            statement.setInt(1, id);
            statement.setQueryTimeout(TIMEOUT_VALUE);
            int count = statement.executeUpdate();
            return count > 0;
        } catch (SQLTimeoutException e) {
            throw new LockWaitTimeoutException();
        }
    }

    public static void searchBooks(BookSearchRequest request, StringBuilder searchSql, BookSearchResponse.Builder responseBuilder) throws SQLException {
        try (Connection connection = ConnectionManager.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(searchSql.toString())) {
                int count = 1;
                if (request.isSearchById()) {
                    preparedStatement.setInt(count++, request.getId());
                }
                if (request.isSearchByTitle()) {
                    preparedStatement.setString(count++, request.getTitle());
                }
                if (request.isSearchByAuthor()) {
                    preparedStatement.setString(count++, request.getAuthor());
                }
                if (request.isSearchByYear()) {
                    preparedStatement.setInt(count++, request.getYear());
                }
                if (request.isSearchByPublisher()) {
                    preparedStatement.setString(count++, request.getPublisher());
                }
                if (request.isLimited()) {
                    preparedStatement.setInt(count, request.getLimit());
                }
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    String title = resultSet.getString("title");
                    String author = resultSet.getString("author");
                    String publisher = resultSet.getString("publisher");
                    int year = resultSet.getInt("year");
                    responseBuilder.addBook(new AddBookRequest(title, author, publisher, year));
                }
            }
        }
    }

}
