package hk.ust.cse.comp4111.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionCommitRequest {
    @JsonProperty("Transaction")
    private final int transactionId;
    @JsonProperty("Operation")
    private final String action;

    public TransactionCommitRequest(int transactionId, String action) {
        this.transactionId = transactionId;
        this.action = action;
    }

    public TransactionCommitRequest() {
        transactionId = 0;
        action = "";
    }

    public int getTransactionId() {
        return transactionId;
    }

    public String getAction() {
        return action;
    }
}
