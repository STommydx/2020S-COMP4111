package hk.ust.cse.comp4111.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseBook {
    private static int id;
    public DatabaseBook() {
        id = 0;
    }

    public static int getId() {
        return id;
    }

    public static void setId(int id) {
        DatabaseBook.id = id;
    }

    public static boolean bookExist(Connection connection, String title, String author, String publisher, int year) throws SQLException{
        boolean exist = false;
        try (PreparedStatement statement = connection.prepareStatement("SELECT id FROM books WHERE title = ? AND author = ? AND publisher = ? AND year = ?")) {
            statement.setString(1, title);
            statement.setString(2, author);
            statement.setString(3, publisher);
            statement.setInt(4, year);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    setId(resultSet.getInt(1));
                    exist = true;
                }
            }
        }
        return exist;



    }








}
