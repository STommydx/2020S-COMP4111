package hk.ust.cse.comp4111.handler;

import hk.ust.cse.comp4111.exception.BadTransactionActionException;
import hk.ust.cse.comp4111.exception.BadTransactionIdException;
import hk.ust.cse.comp4111.exception.InternalServerException;
import hk.ust.cse.comp4111.transaction.TransactionActionRequest;
import hk.ust.cse.comp4111.transaction.TransactionService;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class TransactionActionRequestHandler extends JsonRequestHandler<TransactionActionRequest> {

    public TransactionActionRequestHandler() {
        super(TransactionActionRequest.class);
    }

    @Override
    public void handle(String httpMethod, String path, Map<String, String> param, @NotNull TransactionActionRequest requestBody, HttpResponse response) throws InternalServerException {
        UUID user = UUID.fromString(param.get("token"));
        TransactionService transactionService = TransactionService.getInstance(user);
        try {
            transactionService.addTransactionAction(requestBody);
            response.setStatusCode(HttpStatus.SC_OK);
        } catch (BadTransactionIdException | BadTransactionActionException e) {
            response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
        }
    }
}