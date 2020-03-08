package hk.ust.cse.comp4111.handler;

import hk.ust.cse.comp4111.auth.AuthService;
import hk.ust.cse.comp4111.exception.InternalServerException;
import hk.ust.cse.comp4111.exception.TokenNotFoundException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class AuthRequestHandler extends ServerRequestHandler {

    private ServerRequestHandler requestHandler;

    public AuthRequestHandler(@NotNull ServerRequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public void handle(String httpMethod, String path, Map<String, String> param, @Nullable InputStream requestBody, HttpResponse response) throws IOException, InternalServerException {
        String token = param.getOrDefault("token", null);
        if (token == null) {
            response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
        } else {
            try {
                AuthService.getInstance().getUser(token);
            } catch (TokenNotFoundException e) {
                response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
                return;
            }
            requestHandler.handle(httpMethod, path, param, requestBody, response);
        }
    }
}
