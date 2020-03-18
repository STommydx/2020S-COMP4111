package hk.ust.cse.comp4111.book;

import hk.ust.cse.comp4111.database.ConnectionManager;
import hk.ust.cse.comp4111.database.DatabaseBook;
import hk.ust.cse.comp4111.exception.BookExistException;
import hk.ust.cse.comp4111.exception.BookInvalidStatusException;
import hk.ust.cse.comp4111.exception.BookNotExistException;
import hk.ust.cse.comp4111.exception.InternalServerException;

import java.sql.Connection;
import java.sql.SQLException;

public class BookService {
    private static BookService instance = new BookService();

    public static BookService getInstance() {
        return instance;
    }


    public int addBook(AddBookRequest request) throws BookExistException, InternalServerException {
        String title = request.getTitle();
        String author = request.getAuthor();
        String publisher = request.getPublisher();
        int year = request.getYear();

        try (Connection connection = ConnectionManager.getConnection()) {
            int exist = DatabaseBook.bookExist(connection, title, author, publisher, year);
            if (exist != -1) {
                throw new BookExistException(exist);
            } else {
                return DatabaseBook.addBookRecord(connection, title, author, publisher, year);
            }
        } catch (SQLException e) {
            throw new InternalServerException(e);
        }

    }

    public void putBook(BookPutRequest request, int id) throws InternalServerException, BookNotExistException, BookInvalidStatusException {
        boolean available = request.isAvaliable();
        try (Connection connection = ConnectionManager.getConnection()) {
            connection.setAutoCommit(false);
            boolean curAvailability = DatabaseBook.curAvailability(connection, id);
            if (!available && !curAvailability) {
                connection.rollback();
                throw new BookInvalidStatusException();
            } else if (available && curAvailability) {
                connection.rollback();
                throw new BookInvalidStatusException();
            } else {
                DatabaseBook.updateBookAvailability(connection, id, available);
                connection.commit();
            }
        } catch (SQLException e) {
            throw new InternalServerException(e);
        }
    }

    public void deleteBook(int id) throws BookNotExistException, InternalServerException {
        try (Connection connection = ConnectionManager.getConnection()) {
            boolean bookExist = DatabaseBook.deleteBook(connection, id);
            if (!bookExist) {
                throw new BookNotExistException();
            }
        } catch (SQLException e) {
            throw new InternalServerException(e);
        }
    }


}
