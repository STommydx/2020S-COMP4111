package hk.ust.cse.comp4111.handler;

import hk.ust.cse.comp4111.auth.AuthService;
import hk.ust.cse.comp4111.exception.InternalServerException;
import hk.ust.cse.comp4111.exception.TokenNotFoundException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class LogoutRequestHandler extends ServerRequestHandler {
    @Override
    public void handle(String httpMethod, String path, Map<String, String> param, @Nullable InputStream requestBody, HttpResponse response) {
        if (!httpMethod.equalsIgnoreCase("GET")) {
            response.setStatusCode(HttpStatus.SC_NOT_FOUND);
            return;
        }
        String token = param.get("token");
        if (token == null) {
            response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
            return;
        }
        try {
            AuthService.getInstance().logout(token);
            response.setStatusCode(HttpStatus.SC_OK);
        } catch (TokenNotFoundException e) {
            response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
        }
    }
}
