package hk.ust.cse.comp4111.handler;

import hk.ust.cse.comp4111.exception.BadTransactionActionException;
import hk.ust.cse.comp4111.exception.BadTransactionIdException;
import hk.ust.cse.comp4111.exception.InternalServerException;
import hk.ust.cse.comp4111.transaction.TransactionActionRequest;
import hk.ust.cse.comp4111.transaction.TransactionService;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.nio.AsyncResponseProducer;
import org.apache.hc.core5.http.nio.support.AsyncResponseBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class TransactionActionRequestHandler extends JsonRequestHandler<TransactionActionRequest> {

    public TransactionActionRequestHandler() {
        super(TransactionActionRequest.class);
    }

    @Override
    public AsyncResponseProducer handleJson(String httpMethod, String path, Map<String, String> param, @NotNull TransactionActionRequest requestBody) throws InternalServerException {
        String tokenString = param.get("token");
        if (tokenString == null) throw new InternalServerException(new NullPointerException());
        UUID user = UUID.fromString(tokenString);
        TransactionService transactionService = TransactionService.getInstance(user);
        try {
            transactionService.addTransactionAction(requestBody);
            return AsyncResponseBuilder.create(HttpStatus.SC_OK).build();
        } catch (BadTransactionIdException | BadTransactionActionException e) {
            return AsyncResponseBuilder.create(HttpStatus.SC_BAD_REQUEST).build();
        }
    }
}
