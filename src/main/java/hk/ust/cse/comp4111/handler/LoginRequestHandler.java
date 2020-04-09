package hk.ust.cse.comp4111.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import hk.ust.cse.comp4111.auth.AuthService;
import hk.ust.cse.comp4111.auth.LoginRequest;
import hk.ust.cse.comp4111.auth.LoginResponse;
import hk.ust.cse.comp4111.exception.BadCredentialsException;
import hk.ust.cse.comp4111.exception.DuplicateLoginException;
import hk.ust.cse.comp4111.exception.InternalServerException;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.nio.AsyncResponseProducer;
import org.apache.hc.core5.http.nio.support.AsyncResponseBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class LoginRequestHandler extends JsonRequestHandler<LoginRequest> {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectWriter objectWriter = objectMapper.writer();

    public LoginRequestHandler() {
        super(LoginRequest.class);
    }

    @Override
    public AsyncResponseProducer handleJson(String httpMethod, String path, Map<String, String> param, @NotNull LoginRequest requestBody) throws InternalServerException {
        if (!httpMethod.equalsIgnoreCase("POST")) {
            return AsyncResponseBuilder.create(HttpStatus.SC_NOT_FOUND).build();
        }
        try {
            String token = AuthService.getInstance().login(requestBody);
            LoginResponse loginResponse = new LoginResponse(token);
            return AsyncResponseBuilder.create(HttpStatus.SC_OK)
                .setEntity(objectWriter.writeValueAsBytes(loginResponse), ContentType.create("application/json", "UTF-8"))
                .build();
        } catch (BadCredentialsException e) {
            return AsyncResponseBuilder.create(HttpStatus.SC_BAD_REQUEST).build();
        } catch (DuplicateLoginException e) {
            return AsyncResponseBuilder.create(HttpStatus.SC_CONFLICT).build();
        } catch (JsonProcessingException e) {
            throw new InternalServerException(e);
        }
    }
}
