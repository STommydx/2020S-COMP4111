package hk.ust.cse.comp4111.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import hk.ust.cse.comp4111.book.AddBookRequest;
import hk.ust.cse.comp4111.book.BookService;
import hk.ust.cse.comp4111.exception.BookExistException;
import hk.ust.cse.comp4111.exception.InternalServerException;
import org.apache.http.*;
import org.jetbrains.annotations.NotNull;

import java.util.Map;


public class AddBookRequestHandler extends JsonRequestHandler<AddBookRequest> {

    private static ObjectMapper mapper = new ObjectMapper();

    public AddBookRequestHandler() {
        super(AddBookRequest.class);
    }


    public void handle(String httpMethod, String path, Map<String, String> param, @NotNull AddBookRequest requestBody, HttpResponse response) throws InternalServerException {
        if (!httpMethod.equalsIgnoreCase("POST")) {
            response.setStatusCode(HttpStatus.SC_NOT_FOUND);
            return;
        }

        try {
            int id = BookService.getInstance().addBook(requestBody);
            response.setStatusCode(HttpStatus.SC_CREATED);
            response.addHeader(new Header() {
                @Override
                public HeaderElement[] getElements() throws ParseException {
                    return new HeaderElement[0];
                }

                @Override
                public String getName() {
                    return "Location";
                }

                @Override
                public String getValue() {
                    return "/books/" + id;
                }
            });


        } catch (BookExistException e) {
            response.setStatusCode(HttpStatus.SC_CONFLICT);
            response.addHeader(new Header() {
                @Override
                public HeaderElement[] getElements() throws ParseException {
                    return new HeaderElement[0];
                }

                @Override
                public String getName() {
                    return "Duplicate record";
                }

                @Override
                public String getValue() {
                    return "/books/" + e.getId();
                }
            });
        }
    }
}
