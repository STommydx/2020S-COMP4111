package hk.ust.cse.comp4111.handler;

import hk.ust.cse.comp4111.exception.InternalServerException;
import hk.ust.cse.comp4111.transaction.TransactionCommitRequest;
import org.apache.http.HttpResponse;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class TransactionCommitRequestHandler extends JsonRequestHandler<TransactionCommitRequest> {

    public TransactionCommitRequestHandler() {
        super(TransactionCommitRequest.class);
    }

    @Override
    public void handle(String httpMethod, String path, Map<String, String> param, @NotNull TransactionCommitRequest requestBody, HttpResponse response) throws InternalServerException {
        System.out.println("commit");
    }
}
