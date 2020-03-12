package hk.ust.cse.comp4111.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionNewResponse {
    @JsonProperty("Transaction")
    private final int transactionId;

    public TransactionNewResponse(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getTransactionId() {
        return transactionId;
    }

}
