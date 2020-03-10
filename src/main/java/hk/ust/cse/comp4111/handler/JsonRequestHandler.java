package hk.ust.cse.comp4111.handler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hk.ust.cse.comp4111.exception.InternalServerException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public abstract class JsonRequestHandler<T> extends ServerRequestHandler {

    private static ObjectMapper mapper = new ObjectMapper();
    private Class<T> jsonClass;

    public JsonRequestHandler(Class<T> jsonClass) {
        this.jsonClass = jsonClass;
    }

    @Override
    public void handle(String httpMethod, String path, Map<String, String> param, @Nullable InputStream requestBody, HttpResponse response) throws IOException, InternalServerException {
        if (requestBody == null) {
            response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
            return;
        }
        try {
            T result = mapper.readValue(requestBody, jsonClass);
            handle(httpMethod, path, param, result, response);
        } catch (JsonParseException | JsonMappingException e) {
            response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
        }
    }

    public abstract void handle(String httpMethod, String path, Map<String, String> param, @NotNull T requestBody, HttpResponse response) throws InternalServerException;

}
