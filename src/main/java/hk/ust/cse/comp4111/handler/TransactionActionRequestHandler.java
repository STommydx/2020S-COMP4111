package hk.ust.cse.comp4111.handler;

import hk.ust.cse.comp4111.exception.InternalServerException;
import hk.ust.cse.comp4111.transaction.TransactionActionRequest;
import org.apache.http.HttpResponse;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class TransactionActionRequestHandler extends JsonRequestHandler<TransactionActionRequest> {

    public TransactionActionRequestHandler() {
        super(TransactionActionRequest.class);
    }

    @Override
    public void handle(String httpMethod, String path, Map<String, String> param, @NotNull TransactionActionRequest requestBody, HttpResponse response) throws InternalServerException {
        System.out.println("action");
    }
}
