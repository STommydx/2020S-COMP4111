package hk.ust.cse.comp4111.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import hk.ust.cse.comp4111.exception.InternalServerException;
import hk.ust.cse.comp4111.exception.NoAvailableTransactionException;
import hk.ust.cse.comp4111.transaction.TransactionNewResponse;
import hk.ust.cse.comp4111.transaction.TransactionService;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NByteArrayEntity;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

public class TransactionNewRequestHandler extends ServerRequestHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectWriter objectWriter = objectMapper.writer();

    @Override
    public void handle(String httpMethod, String path, Map<String, String> param, @Nullable InputStream requestBody, HttpResponse response) throws IOException, InternalServerException {
        String tokenString = param.get("token");
        if (tokenString == null) throw new InternalServerException(new NullPointerException());
        UUID user = UUID.fromString(tokenString);
        TransactionService transactionService = TransactionService.getInstance(user);
        int transactionId;
        try {
            transactionId = transactionService.newTransaction().getId();
        } catch (NoAvailableTransactionException e) {
            response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
            return;
        }
        NByteArrayEntity entity = new NByteArrayEntity(objectWriter.writeValueAsBytes(new TransactionNewResponse(transactionId)), ContentType.create("application/json", "UTF-8"));
        response.setStatusCode(HttpStatus.SC_OK);
        response.setEntity(entity);
    }

}
