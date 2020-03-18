package hk.ust.cse.comp4111.handler;


import hk.ust.cse.comp4111.book.BookService;
import hk.ust.cse.comp4111.exception.BookNotExistException;
import hk.ust.cse.comp4111.exception.InternalServerException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class BookDeleteRequestHandler extends ServerRequestHandler {

    public BookDeleteRequestHandler() {
    }

    @Override
    public void handle(String httpMethod, String path, Map<String, String> param, InputStream requestBody, HttpResponse response) throws IOException, InternalServerException {
        if (!httpMethod.equalsIgnoreCase("DELETE")) {
            response.setStatusCode(HttpStatus.SC_NOT_FOUND);
            return;
        }

        String[] temp = path.split("/");
        temp = temp[temp.length - 1].split("\\?");
        String idFromURL = temp[0];

        try {
            BookService.getInstance().deleteBook(Integer.parseInt(idFromURL));
        } catch (BookNotExistException e) {
            response.setStatusCode(HttpStatus.SC_NOT_FOUND);
            response.setReasonPhrase("No book record");
        }
    }
}
