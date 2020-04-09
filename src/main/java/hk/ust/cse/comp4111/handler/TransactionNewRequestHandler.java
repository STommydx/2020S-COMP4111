package hk.ust.cse.comp4111.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import hk.ust.cse.comp4111.exception.InternalServerException;
import hk.ust.cse.comp4111.transaction.TransactionNewResponse;
import hk.ust.cse.comp4111.transaction.TransactionService;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.nio.AsyncResponseProducer;
import org.apache.hc.core5.http.nio.support.AsyncResponseBuilder;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class TransactionNewRequestHandler extends ServerRequestHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectWriter objectWriter = objectMapper.writer();

    @Override
    public AsyncResponseProducer handle(String httpMethod, String path, Map<String, String> param, @Nullable byte[] data) throws IOException, InternalServerException {
        String tokenString = param.get("token");
        if (tokenString == null) throw new InternalServerException(new NullPointerException());
        UUID user = UUID.fromString(tokenString);
        TransactionService transactionService = TransactionService.getInstance(user);
        int transactionId = transactionService.newTransaction().getId();
        return AsyncResponseBuilder.create(HttpStatus.SC_OK)
                .setEntity(objectWriter.writeValueAsBytes(new TransactionNewResponse(transactionId)), ContentType.create("application/json", "UTF-8"))
                .build();
    }

}
