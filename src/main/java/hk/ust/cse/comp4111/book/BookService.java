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
    private static final BookService instance = new BookService();

    public static BookService getInstance() {
        return instance;
    }

    public BookSearchResponse searchBook(BookSearchRequest request) throws SQLException {
        StringBuilder searchSql = new StringBuilder();
        searchSql.append("SELECT title, author, publisher, year FROM books");
        if (request.isSearchById() || request.isSearchByTitle() || request.isSearchByAuthor()) {
            searchSql.append(" WHERE");
            if (request.isSearchById()) {
                searchSql.append(" id=?");
            }
            if (request.isSearchByTitle()) {
                if (request.isSearchById()) {
                    searchSql.append(" AND");
                }
                searchSql.append(" title LIKE CONCAT('%',?,'%')");
            }
            if (request.isSearchByAuthor()) {
                if (request.isSearchById() || request.isSearchByTitle()) {
                    searchSql.append(" AND");
                }
                searchSql.append(" author LIKE CONCAT('%',?,'%')");
            }
        }
        if (request.isSorted()) {
            searchSql.append(" ORDER BY");
            switch (request.getSortType()) {
                case BY_ID:
                    searchSql.append(" id");
                    break;
                case BY_TITLE:
                    searchSql.append(" title");
                    break;
                case BY_AUTHOR:
                    searchSql.append(" author");
                    break;
                case BY_YEAR:
                    searchSql.append(" year");
                    break;
            }
            if (request.isSortReversed()) {
                searchSql.append(" DESC");
            }
        }
        if (request.isLimited()) {
            searchSql.append(" LIMIT ?");
        }

        BookSearchResponse.Builder responseBuilder = new BookSearchResponse.Builder();
        DatabaseBook.searchBookSql(request, searchSql,responseBuilder);

        return responseBuilder.build();
    }

    public int addBook(AddBookRequest request) throws BookExistException, InternalServerException {
        String title = request.getTitle();
        String author = request.getAuthor();
        String publisher = request.getPublisher();
        int year = request.getYear();

        try (Connection connection = ConnectionManager.getConnection()) {
            boolean exist = DatabaseBook.addBookRecord(connection, title, author, publisher, year);
            int id = DatabaseBook.bookExist(connection, title, author, publisher, year); // the two queries is not necessarily required to be atomic
            if (exist) {
                throw new BookExistException(id);
            } else {
                return id;
            }
        } catch (SQLException e) {
            throw new InternalServerException(e);
        }

    }

    public void putBook(BookPutRequest request, int id) throws InternalServerException, BookNotExistException, BookInvalidStatusException {
        boolean available = request.isAvailable();
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
