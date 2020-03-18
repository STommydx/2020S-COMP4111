package hk.ust.cse.comp4111.database;

import hk.ust.cse.comp4111.exception.BookNotExistException;

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


    public static boolean curAvailablity(Connection connection, int id) throws SQLException, BookNotExistException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT id, available FROM books WHERE id = ?")) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(2);
            } else {
                throw new BookNotExistException();
            }
        }

    }

    public static boolean bookExistByID(Connection connection, int id) throws SQLException, BookNotExistException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT id FROM books WHERE id = ?")) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return true;
            } else {
                throw new BookNotExistException();
            }
        }

    }


}
