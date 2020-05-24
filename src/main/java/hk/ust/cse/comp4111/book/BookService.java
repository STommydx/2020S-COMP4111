package hk.ust.cse.comp4111.book;

import hk.ust.cse.comp4111.database.ConnectionManager;
import hk.ust.cse.comp4111.database.DatabaseBook;
import hk.ust.cse.comp4111.exception.*;

import java.sql.Connection;
import java.sql.SQLException;

public class BookService {
    private static final BookService instance = new BookService();

    public static BookService getInstance() {
        return instance;
    }

    public BookSearchResponse searchBook(BookSearchRequest request) throws InternalServerException {
        StringBuilder searchSql = new StringBuilder();
        searchSql.append("SELECT title, author, publisher, year FROM books");
        if (request.isSearchById() || request.isSearchByTitle() || request.isSearchByAuthor() || request.isSearchByPublisher() || request.isSearchByYear()) {
            boolean multiStatement = false;
            searchSql.append(" WHERE");
            if (request.isSearchById()) {
                searchSql.append(" id=?");
                multiStatement = true;
            }
            if (request.isSearchByTitle()) {
                if (multiStatement) {
                    searchSql.append(" AND");
                }
                searchSql.append(" INSTR(title, ?) > 0");
                multiStatement = true;
            }
            if (request.isSearchByAuthor()) {
                if (multiStatement) {
                    searchSql.append(" AND");
                }
                searchSql.append(" INSTR(author, ?) > 0");
                multiStatement = true;
            }
            if (request.isSearchByYear()) {
                if (multiStatement) {
                    searchSql.append(" AND");
                }
                searchSql.append(" year = ?");
                multiStatement = true;
            }
            if (request.isSearchByPublisher()) {
                if (multiStatement) {
                    searchSql.append(" AND");
                }
                searchSql.append(" INSTR(publisher, ?) > 0");
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
                case BY_PUBLISHER:
                    searchSql.append(" publisher");
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

        try (ConnectionManager.ConnectionInstance connectionInstance = ConnectionManager.getInstance().getConnectionInstance()) {
            Connection connection = connectionInstance.getConnection();
            DatabaseBook.searchBooks(connection, request, searchSql, responseBuilder);
        } catch (SQLException | InterruptedException e) {
            throw new InternalServerException(e);
        }

        return responseBuilder.build();
    }

    public int addBook(AddBookRequest request) throws BookExistException, InternalServerException {
        String title = request.getTitle();
        String author = request.getAuthor();
        String publisher = request.getPublisher();
        int year = request.getYear();

        try (ConnectionManager.ConnectionInstance connectionInstance = ConnectionManager.getInstance().getConnectionInstance()) {
            Connection connection = connectionInstance.getConnection();
            boolean exist = DatabaseBook.addBookRecord(connection, title, author, publisher, year);
            int id = DatabaseBook.isBookExist(connection, title, author, publisher, year); // the two queries is not necessarily required to be atomic
            if (exist) {
                throw new BookExistException(id);
            } else {
                return id;
            }
        } catch (SQLException | InterruptedException e) {
            throw new InternalServerException(e);
        }

    }

    public boolean putBook(BookPutRequest request, int id) throws InternalServerException, BookNotExistException, BookInvalidStatusException {
        boolean available = request.isAvailable();
        try (ConnectionManager.ConnectionInstance connectionInstance = ConnectionManager.getInstance().getConnectionInstance()) {
            Connection connection = connectionInstance.getConnection();
            connection.setAutoCommit(false);
            boolean curAvailability = DatabaseBook.isBookCurrentlyAvailable(connection, id);
            if (!available && !curAvailability) {
                connection.rollback();
                throw new BookInvalidStatusException();
            } else if (available && curAvailability) {
                connection.rollback();
                throw new BookInvalidStatusException();
            } else {
                DatabaseBook.updateBookAvailability(connection, id, available);
                connection.commit();
                return true;
            }
        } catch (LockWaitTimeoutException e) {
            return false;
        } catch (SQLException | InterruptedException e) {
            throw new InternalServerException(e);
        }
    }

    public boolean deleteBook(int id) throws BookNotExistException, InternalServerException {
        try (ConnectionManager.ConnectionInstance connectionInstance = ConnectionManager.getInstance().getConnectionInstance()) {
            Connection connection = connectionInstance.getConnection();
            boolean bookExist = DatabaseBook.deleteBook(connection, id);
            if (!bookExist) {
                throw new BookNotExistException();
            }
            return true;
        } catch (LockWaitTimeoutException e) {
            return false;
        } catch (SQLException | InterruptedException e) {
            throw new InternalServerException(e);
        }
    }
}
