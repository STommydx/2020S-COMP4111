package hk.ust.cse.comp4111.handler;

import hk.ust.cse.comp4111.book.AddBookRequest;
import hk.ust.cse.comp4111.book.BookService;
import hk.ust.cse.comp4111.exception.BookExistException;
import hk.ust.cse.comp4111.exception.InternalServerException;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.nio.AsyncResponseProducer;
import org.apache.hc.core5.http.nio.support.AsyncResponseBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Map;


public class AddBookRequestHandler extends JsonRequestHandler<AddBookRequest> {

    public AddBookRequestHandler() {
        super(AddBookRequest.class);
    }


    public AsyncResponseProducer handleJson(String httpMethod, String path, Map<String, String> param, @NotNull AddBookRequest requestBody) throws InternalServerException {
        if (!httpMethod.equalsIgnoreCase("POST")) {
            return AsyncResponseBuilder.create(HttpStatus.SC_NOT_FOUND).build();
        }

        try {
            int id = BookService.getInstance().addBook(requestBody);
            return AsyncResponseBuilder.create(HttpStatus.SC_CREATED)
                    .setHeader("Location", "/books/" + id)
                    .build();

        } catch (BookExistException e) {
            return AsyncResponseBuilder.create(HttpStatus.SC_CONFLICT)
                    .setHeader("Duplicate record", "/books/" + e.getId())
                    .build();
        }
    }
}
