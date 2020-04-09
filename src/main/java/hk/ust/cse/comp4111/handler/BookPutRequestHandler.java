package hk.ust.cse.comp4111.handler;

import hk.ust.cse.comp4111.book.BookPutRequest;
import hk.ust.cse.comp4111.book.BookService;
import hk.ust.cse.comp4111.exception.BookInvalidStatusException;
import hk.ust.cse.comp4111.exception.BookNotExistException;
import hk.ust.cse.comp4111.exception.InternalServerException;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.nio.AsyncResponseProducer;
import org.apache.hc.core5.http.nio.support.AsyncResponseBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class BookPutRequestHandler extends JsonRequestHandler<BookPutRequest> {

    public BookPutRequestHandler() {
        super(BookPutRequest.class);
    }


    @Override
    public AsyncResponseProducer handleJson(String httpMethod, String path, Map<String, String> param, @NotNull BookPutRequest requestBody) throws InternalServerException {
        if (!httpMethod.equalsIgnoreCase("PUT")) {
            return AsyncResponseBuilder.create(HttpStatus.SC_NOT_FOUND).build();
        }

        String[] temp = path.split("/");
        temp = temp[temp.length - 1].split("\\?");
        String idFromURL = temp[0];

        try {
            BookService.getInstance().putBook(requestBody, Integer.parseInt(idFromURL));
            return AsyncResponseBuilder.create(HttpStatus.SC_OK).build();
        } catch (BookNotExistException e) {
            return AsyncResponseBuilder.create(HttpStatus.SC_NOT_FOUND).build();
        } catch (BookInvalidStatusException | NumberFormatException e) {
            return AsyncResponseBuilder.create(HttpStatus.SC_BAD_REQUEST).build();
        }
    }


}
