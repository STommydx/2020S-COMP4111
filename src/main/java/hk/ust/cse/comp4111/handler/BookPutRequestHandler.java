package hk.ust.cse.comp4111.handler;

import hk.ust.cse.comp4111.auth.BookPutRequest;
import hk.ust.cse.comp4111.auth.BookService;
import hk.ust.cse.comp4111.exception.BookInvalidStatusException;
import hk.ust.cse.comp4111.exception.BookNotExistException;
import hk.ust.cse.comp4111.exception.InternalServerException;
import org.apache.http.*;

import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BookPutRequestHandler extends JsonRequestHandler<BookPutRequest> {

    private static ObjectMapper mapper = new ObjectMapper();

    public BookPutRequestHandler() {
        super(BookPutRequest.class);
    }


    @Override
    public void handle(String httpMethod, String path, Map<String, String> param, BookPutRequest requestBody, HttpResponse response) throws InternalServerException {
        if (!httpMethod.equalsIgnoreCase("PUT")) {
            response.setStatusCode(HttpStatus.SC_NOT_FOUND);
            return;
        }

        String[] temp = path.split("/");
        temp = temp[temp.length - 1].split("\\?");
        String idFromURL = temp[0];

        try {
            BookService.getInstance().BookPutRequest(requestBody, Integer.parseInt(idFromURL));
        } catch (BookNotExistException e) {
            response.setStatusCode(HttpStatus.SC_NOT_FOUND);
            response.setReasonPhrase("No book record");
        } catch (BookInvalidStatusException e){
            response.setStatusCode(HttpStatus.SC_BAD_REQUEST);

        }
    }


}
