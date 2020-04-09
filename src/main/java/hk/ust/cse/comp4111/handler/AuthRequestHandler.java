package hk.ust.cse.comp4111.handler;

import hk.ust.cse.comp4111.auth.AuthService;
import hk.ust.cse.comp4111.exception.InternalServerException;
import hk.ust.cse.comp4111.exception.TokenNotFoundException;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.nio.AsyncResponseProducer;
import org.apache.hc.core5.http.nio.support.AsyncResponseBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;

public class AuthRequestHandler extends ServerRequestHandler {

    private final ServerRequestHandler requestHandler;

    public AuthRequestHandler(@NotNull ServerRequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public AsyncResponseProducer handle(String httpMethod, String path, Map<String, String> param, @Nullable byte[] requestBody) throws IOException, InternalServerException {
        String token = param.get("token");
        if (token == null) {
            return AsyncResponseBuilder.create(HttpStatus.SC_BAD_REQUEST).build();
        } else {
            try {
                AuthService.getInstance().getUser(token);
            } catch (TokenNotFoundException e) {
                return AsyncResponseBuilder.create(HttpStatus.SC_BAD_REQUEST).build();
            }
            return requestHandler.handle(httpMethod, path, param, requestBody);
        }
    }
}
