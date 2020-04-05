package hk.ust.cse.comp4111.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import hk.ust.cse.comp4111.book.BookPutRequest;
import hk.ust.cse.comp4111.book.BookService;
import hk.ust.cse.comp4111.exception.BookInvalidStatusException;
import hk.ust.cse.comp4111.exception.BookNotExistException;
import hk.ust.cse.comp4111.exception.InternalServerException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class BookPutRequestHandler extends JsonRequestHandler<BookPutRequest> {

    private static ObjectMapper mapper = new ObjectMapper();

    public BookPutRequestHandler() {
        super(BookPutRequest.class);
    }


    @Override
    public void handleJson(String httpMethod, String path, Map<String, String> param, @NotNull BookPutRequest requestBody, HttpResponse response) throws InternalServerException {
        if (!httpMethod.equalsIgnoreCase("PUT")) {
            response.setStatusCode(HttpStatus.SC_NOT_FOUND);
            return;
        }

        String[] temp = path.split("/");
        temp = temp[temp.length - 1].split("\\?");
        String idFromURL = temp[0];

        try {
            BookService.getInstance().putBook(requestBody, Integer.parseInt(idFromURL));
        } catch (BookNotExistException e) {
            response.setStatusCode(HttpStatus.SC_NOT_FOUND);
            response.setReasonPhrase("No book record");
        } catch (BookInvalidStatusException e) {
            response.setStatusCode(HttpStatus.SC_BAD_REQUEST);

        }
    }


}
