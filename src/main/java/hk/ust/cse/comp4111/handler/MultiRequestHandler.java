package hk.ust.cse.comp4111.handler;

import hk.ust.cse.comp4111.exception.InternalServerException;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.nio.AsyncResponseProducer;
import org.apache.hc.core5.http.nio.support.AsyncResponseBuilder;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
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
    public AsyncResponseProducer handle(String httpMethod, String path, Map<String, String> param, @Nullable byte[] requestBody) throws IOException, InternalServerException {
        if (httpMethod.equalsIgnoreCase("GET") && getRequest != null) {
            return getRequest.handle(httpMethod, path, param, requestBody);
        } else if (httpMethod.equalsIgnoreCase("POST") && postRequest != null) {
            return postRequest.handle(httpMethod, path, param, requestBody);
        } else if (httpMethod.equalsIgnoreCase("PUT") && putRequest != null) {
            return putRequest.handle(httpMethod, path, param, requestBody);
        } else if (httpMethod.equalsIgnoreCase("DELETE") && deleteRequest != null) {
            return deleteRequest.handle(httpMethod, path, param, requestBody);
        } else {
            return AsyncResponseBuilder.create(HttpStatus.SC_NOT_FOUND).build();
        }
    }
}
