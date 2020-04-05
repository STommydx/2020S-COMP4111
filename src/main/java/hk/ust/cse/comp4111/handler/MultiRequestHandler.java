package hk.ust.cse.comp4111.handler;

import hk.ust.cse.comp4111.exception.InternalServerException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class MultiRequestHandler extends ServerRequestHandler {

    @Nullable
    private ServerRequestHandler getRequest;
    @Nullable
    private ServerRequestHandler postRequest;
    @Nullable
    private ServerRequestHandler putRequest;
    @Nullable
    private ServerRequestHandler deleteRequest;

    public MultiRequestHandler() {
        getRequest = null;
        postRequest = null;
        putRequest = null;
        deleteRequest = null;
    }

    public void registerGetHandler(ServerRequestHandler handler) {
        getRequest = handler;
    }

    public void registerPostHandler(ServerRequestHandler handler) {
        postRequest = handler;
    }

    public void registerPutHandler(ServerRequestHandler handler) {
        putRequest = handler;
    }

    public void registerDeleteHandler(ServerRequestHandler handler) {
        deleteRequest = handler;
    }

    @Override
    public void handle(String httpMethod, String path, Map<String, String> param, @Nullable InputStream requestBody, HttpResponse response) throws IOException, InternalServerException {
        if (httpMethod.equalsIgnoreCase("GET") && getRequest != null) {
            getRequest.handle(httpMethod, path, param, requestBody, response);
        } else if (httpMethod.equalsIgnoreCase("POST") && postRequest != null) {
            postRequest.handle(httpMethod, path, param, requestBody, response);
        } else if (httpMethod.equalsIgnoreCase("PUT") && putRequest != null) {
            putRequest.handle(httpMethod, path, param, requestBody, response);
        } else if (httpMethod.equalsIgnoreCase("DELETE") && deleteRequest != null) {
            deleteRequest.handle(httpMethod, path, param, requestBody, response);
        } else {
            response.setStatusCode(HttpStatus.SC_NOT_FOUND);
        }
    }
}
