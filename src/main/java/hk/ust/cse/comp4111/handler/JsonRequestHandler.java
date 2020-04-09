package hk.ust.cse.comp4111.handler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import hk.ust.cse.comp4111.exception.InternalServerException;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.nio.AsyncResponseProducer;
import org.apache.hc.core5.http.nio.support.AsyncResponseBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;

public abstract class JsonRequestHandler<T> extends ServerRequestHandler {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final ObjectReader reader;

    public JsonRequestHandler(Class<T> jsonClass) {
        reader = mapper.readerFor(jsonClass);
    }

    @Override
    public AsyncResponseProducer handle(String httpMethod, String path, Map<String, String> param, @Nullable byte[] requestBody) throws IOException, InternalServerException {
        if (requestBody == null) {
            return AsyncResponseBuilder.create(HttpStatus.SC_BAD_REQUEST).build();
        }
        try {
            T result = reader.readValue(requestBody);
            return handleJson(httpMethod, path, param, result);
        } catch (JsonParseException | JsonMappingException e) {
            return AsyncResponseBuilder.create(HttpStatus.SC_BAD_REQUEST).build();
        }
    }

    public abstract AsyncResponseProducer handleJson(String httpMethod, String path, Map<String, String> param, @NotNull T requestBody) throws InternalServerException;

}
