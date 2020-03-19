package hk.ust.cse.comp4111.book;

import hk.ust.cse.comp4111.database.ConnectionManager;
import hk.ust.cse.comp4111.database.DatabaseBook;
import hk.ust.cse.comp4111.exception.BookExistException;
import hk.ust.cse.comp4111.exception.BookInvalidStatusException;
import hk.ust.cse.comp4111.exception.BookNotExistException;
import hk.ust.cse.comp4111.exception.InternalServerException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BookService {
    private static BookService instance = new BookService();

    public static BookService getInstance() {
        return instance;
    }

    public static BookSearchResponse searchBook(BookSearchRequest request) throws SQLException {
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
            if (request.getSortType() == BookSearchRequest.SortType.BY_ID) {
                searchSql.append(" id");
            } else if (request.getSortType() == BookSearchRequest.SortType.BY_TITLE) {
                searchSql.append(" title");
            } else {
                searchSql.append(" author");
            }
            if (request.isSortReversed()) {
                searchSql.append(" DESC");
            }
        }
        if (request.isLimited()) {
            searchSql.append(" LIMIT ?");
        }

        BookSearchResponse.Builder responseBuilder = new BookSearchResponse.Builder();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionManager.getConnection();
            preparedStatement = connection.prepareStatement(searchSql.toString());
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
            if (request.isLimited()) {
                preparedStatement.setInt(count, request.getLimit());
            }
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                String publisher = resultSet.getString("publisher");
                int year = resultSet.getInt("year");
                responseBuilder.addBook(new AddBookRequest(title, author, publisher, year));
            }
        } finally {
            if (resultSet != null)
                resultSet.close();
            if (preparedStatement != null)
                preparedStatement.close();
            ;
            if (connection != null) {
                connection.close();
            }
        }
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
