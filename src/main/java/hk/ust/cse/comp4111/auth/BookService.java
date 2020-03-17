package hk.ust.cse.comp4111.auth;

import hk.ust.cse.comp4111.database.ConnectionManager;
import hk.ust.cse.comp4111.database.DatabaseBook;
import hk.ust.cse.comp4111.exception.BookExistException;
import hk.ust.cse.comp4111.exception.BookInvalidStatusException;
import hk.ust.cse.comp4111.exception.BookNotExistException;
import hk.ust.cse.comp4111.exception.InternalServerException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class BookService {
    private int id;
    private boolean available;

    private static BookService instance = new BookService();
    public static BookService getInstance() {
        return instance;
    }


    public int addBook(AddBookRequest request) throws BookExistException,InternalServerException{
        String title = request.getTitle();
        String author = request.getAuthor();
        String publisher = request.getPublisher();
        int year = request.getYear();

        try (Connection connection = ConnectionManager.getConnection()) {
            boolean exist = DatabaseBook.bookExist(connection, title, author, publisher,year);
            if (exist) {
                id = DatabaseBook.getId();
                throw new BookExistException(id);
            }

            else if(!exist) {
                try (PreparedStatement statement = connection.prepareStatement("INSERT INTO books (title, author, publisher,year) VALUES (?,?,?,?)")) {
                    statement.setString(1, title);
                    statement.setString(2, author);
                    statement.setString(3, publisher);
                    statement.setInt(4, year);

                    statement.execute();
                    statement.close();

                    exist = DatabaseBook.bookExist(connection, title, author, publisher,year);
                    if (exist) {
                        id = DatabaseBook.getId();
                    }
                }
            }
        } catch (SQLException e) {
            throw new InternalServerException(e);
        }

        return id;
    }

    public void BookPutRequest(BookPutRequest request, int id) throws InternalServerException,BookNotExistException, BookInvalidStatusException {
        available = request.isAvaliable();
        try (Connection connection = ConnectionManager.getConnection()) {
            boolean curAvailabilty = DatabaseBook.curAvailablity(connection, id);
            if (available == false && curAvailabilty == false) {
                throw new BookInvalidStatusException();
            }
            else if(available == true && curAvailabilty == true){
                    throw new BookInvalidStatusException();
            }
            else{
                PreparedStatement statement = connection.prepareStatement("UPDATE books SET available = ? WHERE id = ?");
                statement.setBoolean(1, available);
                statement.setInt(2, id);
                statement.execute();
                statement.close();

                }
        } catch (SQLException e){

        }
    }

    public void BookDeleteRequest(int id) throws BookNotExistException{
        try (Connection connection = ConnectionManager.getConnection()) {
            boolean bookExist = DatabaseBook.bookExistByID(connection, id);
            if(bookExist==true){
                PreparedStatement statement = connection.prepareStatement("DELETE FROM books WHERE id = ?");
                statement.setInt(1, id);
                statement.execute();
                statement.close();
            }
            else{
                throw new BookNotExistException();
            }
        } catch (SQLException e) {
        }
    }


    public int getId() {
        return id;
    }

    public boolean isAvailable() {
        return available;
    }
}
