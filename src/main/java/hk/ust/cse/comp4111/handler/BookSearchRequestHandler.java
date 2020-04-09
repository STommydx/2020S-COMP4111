package hk.ust.cse.comp4111.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import hk.ust.cse.comp4111.book.BookSearchRequest;
import hk.ust.cse.comp4111.book.BookSearchResponse;
import hk.ust.cse.comp4111.book.BookService;
import hk.ust.cse.comp4111.exception.InternalServerException;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.nio.AsyncResponseProducer;
import org.apache.hc.core5.http.nio.support.AsyncResponseBuilder;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.Map;


public class BookSearchRequestHandler extends ServerRequestHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectWriter objectWriter;

    public BookSearchRequestHandler() {
        objectWriter = objectMapper.writer();
    }

    @Override
    public AsyncResponseProducer handle(String httpMethod, String path, Map<String, String> param, @Nullable byte[] requestBody) throws InternalServerException {
        if (!httpMethod.equalsIgnoreCase("GET")) {
            return AsyncResponseBuilder.create(HttpStatus.SC_NOT_FOUND).build();
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
            }
            if (param.containsKey("order") && param.get("order").equals("desc")) {
                builder.reverseSort();
            }
            if (param.containsKey("limit")) {
                builder.limit(Integer.parseInt(param.get("limit")));
            }
            BookSearchResponse searchResponse = BookService.getInstance().searchBook(builder.build());
            if (searchResponse.getTotalNumBooks() == 0) {
                return AsyncResponseBuilder.create(HttpStatus.SC_NO_CONTENT).build();
            } else {
                return AsyncResponseBuilder.create(HttpStatus.SC_OK)
                        .setEntity(objectWriter.writeValueAsBytes(searchResponse), ContentType.create("application/json", "UTF-8"))
                        .build();
            }
        } catch (SQLException | JsonProcessingException e) {
            throw new InternalServerException(e);
        } catch (NumberFormatException e) {
            return AsyncResponseBuilder.create(HttpStatus.SC_BAD_REQUEST).build();
        }
    }
}
