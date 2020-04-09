package hk.ust.cse.comp4111.handler;

import hk.ust.cse.comp4111.exception.InternalServerException;
import org.apache.hc.core5.http.nio.AsyncResponseProducer;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;

public class TransactionPostRequestHandler extends ServerRequestHandler {

    private final ServerRequestHandler newRequestHandler = new TransactionNewRequestHandler();
    private final ServerRequestHandler commitRequestHandler = new TransactionCommitRequestHandler();

    @Override
    public AsyncResponseProducer handle(String httpMethod, String path, Map<String, String> param, @Nullable byte[] requestBody) throws IOException, InternalServerException {
        if (requestBody == null) {
            return newRequestHandler.handle(httpMethod, path, param, null);
        } else {
            return commitRequestHandler.handle(httpMethod, path, param, requestBody);
        }
    }
}
