package hk.ust.cse.comp4111.handler;

import hk.ust.cse.comp4111.auth.AuthService;
import hk.ust.cse.comp4111.exception.TokenNotFoundException;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.nio.AsyncResponseProducer;
import org.apache.hc.core5.http.nio.support.AsyncResponseBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class LogoutRequestHandler extends ServerRequestHandler {
    @Override
    public AsyncResponseProducer handle(String httpMethod, String path, Map<String, String> param, @Nullable byte[] requestBody) {
        if (!httpMethod.equalsIgnoreCase("GET")) {
            return AsyncResponseBuilder.create(HttpStatus.SC_NOT_FOUND).build();
        }
        String token = param.get("token");
        if (token == null) {
            return AsyncResponseBuilder.create(HttpStatus.SC_BAD_REQUEST).build();
        }
        try {
            AuthService.getInstance().logout(token);
            return AsyncResponseBuilder.create(HttpStatus.SC_OK).build();
        } catch (TokenNotFoundException e) {
            return AsyncResponseBuilder.create(HttpStatus.SC_BAD_REQUEST).build();
        }
    }
}
