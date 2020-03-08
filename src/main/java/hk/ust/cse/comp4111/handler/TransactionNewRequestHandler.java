package hk.ust.cse.comp4111.handler;

import hk.ust.cse.comp4111.exception.InternalServerException;
import org.apache.http.HttpResponse;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class TransactionNewRequestHandler extends ServerRequestHandler {
    @Override
    public void handle(String httpMethod, String path, Map<String, String> param, @Nullable InputStream requestBody, HttpResponse response) throws IOException, InternalServerException {
        System.out.println("new");
    }
}
