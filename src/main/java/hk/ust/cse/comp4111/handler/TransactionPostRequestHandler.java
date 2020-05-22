package hk.ust.cse.comp4111.handler;

import hk.ust.cse.comp4111.exception.InternalServerException;
import hk.ust.cse.comp4111.exception.LockWaitTimeoutException;
import org.apache.http.HttpResponse;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class TransactionPostRequestHandler extends ServerRequestHandler {

    private final ServerRequestHandler newRequestHandler = new TransactionNewRequestHandler();
    private final ServerRequestHandler commitRequestHandler = new TransactionCommitRequestHandler();

    @Override
    public void handle(String httpMethod, String path, Map<String, String> param, @Nullable InputStream requestBody, HttpResponse response) throws IOException, InternalServerException, LockWaitTimeoutException {
        if (requestBody == null) {
            newRequestHandler.handle(httpMethod, path, param, null, response);
        } else {
            commitRequestHandler.handle(httpMethod, path, param, requestBody, response);
        }
    }
}
