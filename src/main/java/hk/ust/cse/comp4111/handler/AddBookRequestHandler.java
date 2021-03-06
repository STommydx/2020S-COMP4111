package hk.ust.cse.comp4111.handler;

import hk.ust.cse.comp4111.book.AddBookRequest;
import hk.ust.cse.comp4111.book.BookService;
import hk.ust.cse.comp4111.exception.BookExistException;
import hk.ust.cse.comp4111.exception.InternalServerException;
import org.apache.http.*;
import org.jetbrains.annotations.NotNull;

import java.util.Map;


public class AddBookRequestHandler extends JsonRequestHandler<AddBookRequest> {

    public AddBookRequestHandler() {
        super(AddBookRequest.class);
    }


    public void handleJson(String httpMethod, String path, Map<String, String> param, @NotNull AddBookRequest requestBody, HttpResponse response) throws InternalServerException {
        if (!httpMethod.equalsIgnoreCase("POST")) {
            response.setStatusCode(HttpStatus.SC_NOT_FOUND);
            return;
        }

        try {
            int id = BookService.getInstance().addBook(requestBody);
            response.setStatusCode(HttpStatus.SC_CREATED);
            response.setHeader("Location", "/books/" + id);
        } catch (BookExistException e) {
            response.setStatusCode(HttpStatus.SC_CONFLICT);
            response.setHeader("Duplicate record", "/books/" + e.getId());
        }
    }
}
