package hk.ust.cse.comp4111.handler;

import hk.ust.cse.comp4111.exception.BadCommitException;
import hk.ust.cse.comp4111.exception.BadTransactionIdException;
import hk.ust.cse.comp4111.exception.InternalServerException;
import hk.ust.cse.comp4111.transaction.TransactionCommitRequest;
import hk.ust.cse.comp4111.transaction.TransactionService;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class TransactionCommitRequestHandler extends JsonRequestHandler<TransactionCommitRequest> {

    public TransactionCommitRequestHandler() {
        super(TransactionCommitRequest.class);
    }

    @Override
    public void handleJson(String httpMethod, String path, Map<String, String> param, @NotNull TransactionCommitRequest requestBody, HttpResponse response) throws InternalServerException {
        String tokenString = param.get("token");
        if (tokenString == null) throw new InternalServerException(new NullPointerException());
        UUID user = UUID.fromString(tokenString);
        TransactionService transactionService = TransactionService.getInstance(user);
        switch (requestBody.getAction()) {
            case "commit":
                try {
                    transactionService.commitTransaction(requestBody.getTransactionId());
                    response.setStatusCode(HttpStatus.SC_OK);
                } catch (BadTransactionIdException | BadCommitException e) {
                    response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
                }
                break;
            case "cancel":
                try {
                    transactionService.cancelTransaction(requestBody.getTransactionId());
                } catch (BadTransactionIdException e) {
                    response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
                }
                break;
            default:
                response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
        }
    }
}
