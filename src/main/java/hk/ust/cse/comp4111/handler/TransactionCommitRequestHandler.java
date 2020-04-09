package hk.ust.cse.comp4111.handler;

import hk.ust.cse.comp4111.exception.BadCommitException;
import hk.ust.cse.comp4111.exception.BadTransactionIdException;
import hk.ust.cse.comp4111.exception.InternalServerException;
import hk.ust.cse.comp4111.transaction.TransactionCommitRequest;
import hk.ust.cse.comp4111.transaction.TransactionService;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.nio.AsyncResponseProducer;
import org.apache.hc.core5.http.nio.support.AsyncResponseBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class TransactionCommitRequestHandler extends JsonRequestHandler<TransactionCommitRequest> {

    public TransactionCommitRequestHandler() {
        super(TransactionCommitRequest.class);
    }

    @Override
    public AsyncResponseProducer handleJson(String httpMethod, String path, Map<String, String> param, @NotNull TransactionCommitRequest requestBody) throws InternalServerException {
        String tokenString = param.get("token");
        if (tokenString == null) throw new InternalServerException(new NullPointerException());
        UUID user = UUID.fromString(tokenString);
        TransactionService transactionService = TransactionService.getInstance(user);
        switch (requestBody.getAction()) {
            case "commit":
                try {
                    transactionService.commitTransaction(requestBody.getTransactionId());
                    return AsyncResponseBuilder.create(HttpStatus.SC_OK).build();
                } catch (BadTransactionIdException | BadCommitException e) {
                    return AsyncResponseBuilder.create(HttpStatus.SC_BAD_REQUEST).build();
                }
            case "cancel":
                try {
                    transactionService.cancelTransaction(requestBody.getTransactionId());
                    return AsyncResponseBuilder.create(HttpStatus.SC_OK).build();
                } catch (BadTransactionIdException e) {
                    return AsyncResponseBuilder.create(HttpStatus.SC_BAD_REQUEST).build();
                }
            default:
                return AsyncResponseBuilder.create(HttpStatus.SC_BAD_REQUEST).build();
        }
    }
}
