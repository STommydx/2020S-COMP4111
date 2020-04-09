package hk.ust.cse.comp4111.handler;


import hk.ust.cse.comp4111.book.BookService;
import hk.ust.cse.comp4111.exception.BookNotExistException;
import hk.ust.cse.comp4111.exception.InternalServerException;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.nio.AsyncResponseProducer;
import org.apache.hc.core5.http.nio.support.AsyncResponseBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class BookDeleteRequestHandler extends ServerRequestHandler {

    public BookDeleteRequestHandler() {
    }

    @Override
    public AsyncResponseProducer handle(String httpMethod, String path, Map<String, String> param, @Nullable byte[] requestBody) throws InternalServerException {
        if (!httpMethod.equalsIgnoreCase("DELETE")) {
            return AsyncResponseBuilder.create(HttpStatus.SC_NOT_FOUND).build();
        }

        String[] temp = path.split("/");
        temp = temp[temp.length - 1].split("\\?");
        String idFromURL = temp[0];

        try {
            BookService.getInstance().deleteBook(Integer.parseInt(idFromURL));
            return AsyncResponseBuilder.create(HttpStatus.SC_OK).build();
        } catch (BookNotExistException e) {
            return AsyncResponseBuilder.create(HttpStatus.SC_NOT_FOUND).build();
        } catch (NumberFormatException e){
            return AsyncResponseBuilder.create(HttpStatus.SC_BAD_REQUEST).build();
        }
    }
}
