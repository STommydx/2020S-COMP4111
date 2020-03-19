package hk.ust.cse.comp4111.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hk.ust.cse.comp4111.book.BookSearchRequest;
import hk.ust.cse.comp4111.book.BookSearchResponse;
import hk.ust.cse.comp4111.book.BookService;
import hk.ust.cse.comp4111.exception.InternalServerException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.Map;


public class BookSearchRequestHandler extends ServerRequestHandler {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public BookSearchRequestHandler() {
    }

    @Override
    public void handle(String httpMethod, String path, Map<String, String> param, @Nullable InputStream requestBody, HttpResponse response) throws InternalServerException {
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
            if (param.containsKey("sortby")) {
                String sortby = param.get("sortby");
                if (sortby == "id") {
                    builder.sortById();
                }
                if (sortby == "title") {
                    builder.sortByTitle();
                }
                if (sortby == "author") {
                    builder.sortByAuthor();
                }
            }
            if (param.containsKey("order") && param.get("order") == "desc") {
                builder.reverseSort();
            }
            if (param.containsKey("limit")) {
                builder.limit(Integer.parseInt(param.get("limit")));
            }
            BookSearchResponse searchResponse = BookService.getInstance().searchBook(builder.build());
            if (searchResponse.getTotalNumBooks() == 0) {
                response.setStatusCode(HttpStatus.SC_NO_CONTENT);
            } else {
                response.setStatusCode(HttpStatus.SC_OK);
                response.setEntity(new NStringEntity(objectMapper.writeValueAsString(searchResponse), ContentType.create("application/json", "UTF-8")));
            }
        } catch (SQLException e) {
        } catch (JsonProcessingException e) {
            throw new InternalServerException(e);
        }
    }
}
