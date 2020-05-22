package hk.ust.cse.comp4111.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import hk.ust.cse.comp4111.book.BookSearchRequest;
import hk.ust.cse.comp4111.book.BookSearchResponse;
import hk.ust.cse.comp4111.book.BookService;
import hk.ust.cse.comp4111.exception.InternalServerException;
import hk.ust.cse.comp4111.exception.LockWaitTimeoutException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NByteArrayEntity;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.Map;


public class BookSearchRequestHandler extends ServerRequestHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectWriter objectWriter = objectMapper.writer();

    @Override
    public void handle(String httpMethod, String path, Map<String, String> param, @Nullable InputStream requestBody, HttpResponse response) throws InternalServerException, LockWaitTimeoutException {
        if (!httpMethod.equalsIgnoreCase("GET")) {
            response.setStatusCode(HttpStatus.SC_NOT_FOUND);
            return;
        }
        try {
            BookSearchRequest.Builder builder = new BookSearchRequest.Builder();
            if (param.containsKey("id")) {
                builder.id(Integer.parseInt(param.get("id")));
            }
            if (param.containsKey("title")) {
                builder.title(param.get("title"));
            }
            if (param.containsKey("author")) {
                builder.author(param.get("author"));
            }
            if (param.containsKey("year")) {
                builder.year(Integer.parseInt(param.get("year")));
            }
            if (param.containsKey("publisher")) {
                builder.publisher(param.get("publisher"));
            }
            if (param.containsKey("sortby")) {
                String sortby = param.get("sortby");
                if (sortby.equals("id")) {
                    builder.sortById();
                }
                if (sortby.equals("title")) {
                    builder.sortByTitle();
                }
                if (sortby.equals("author")) {
                    builder.sortByAuthor();
                }
                if (sortby.equals("year")) {
                    builder.sortByYear();
                }
                if (sortby.equals("publisher")) {
                    builder.sortByPublisher();
                }
            }
            if (param.containsKey("order") && param.get("order").equals("desc")) {
                builder.reverseSort();
            }
            if (param.containsKey("limit")) {
                int limit = Integer.parseInt(param.get("limit"));
                if (limit < 0) throw new NumberFormatException();
                builder.limit(limit);
            }
            BookSearchResponse searchResponse = BookService.getInstance().searchBook(builder.build());
            if (searchResponse.getTotalNumBooks() == 0) {
                response.setStatusCode(HttpStatus.SC_NO_CONTENT);
            } else {
                response.setStatusCode(HttpStatus.SC_OK);
                response.setEntity(new NByteArrayEntity(objectWriter.writeValueAsBytes(searchResponse), ContentType.create("application/json", "UTF-8")));
            }
        } catch (SQLException | JsonProcessingException e) {
            throw new InternalServerException(e);
        } catch (NumberFormatException e) {
            response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
        }
    }
}
