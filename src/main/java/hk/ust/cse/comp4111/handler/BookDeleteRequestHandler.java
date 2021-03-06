package hk.ust.cse.comp4111.handler;


import hk.ust.cse.comp4111.book.BookService;
import hk.ust.cse.comp4111.exception.BookNotExistException;
import hk.ust.cse.comp4111.exception.InternalServerException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.Map;

public class BookDeleteRequestHandler extends ServerRequestHandler {

    private static final String PATH_PREFIX = "/BookManagementService/books/";

    public BookDeleteRequestHandler() {
    }

    @Override
    public void handle(String httpMethod, String path, Map<String, String> param, @Nullable InputStream requestBody, HttpResponse response) throws InternalServerException {
        if (!httpMethod.equalsIgnoreCase("DELETE")) {
            response.setStatusCode(HttpStatus.SC_NOT_FOUND);
            return;
        }

        if (!path.startsWith(PATH_PREFIX)) {
            response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        String idFromURL = path.substring(PATH_PREFIX.length());

        try {
            boolean success = BookService.getInstance().deleteBook(Integer.parseInt(idFromURL));
            if (!success) {
                response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
                return;
            }
            response.setStatusCode(HttpStatus.SC_OK);
        } catch (BookNotExistException e) {
            response.setStatusCode(HttpStatus.SC_NOT_FOUND);
            response.setReasonPhrase("No book record");
        } catch (NumberFormatException e){
            response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
        }
    }
}
