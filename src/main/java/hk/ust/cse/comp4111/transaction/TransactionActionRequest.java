package hk.ust.cse.comp4111.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionActionRequest {
    @JsonProperty("Transaction")
    private final int transactionId;
    @JsonProperty("Book")
    private final int bookId;
    @JsonProperty("Action")
    private final String action;

    public TransactionActionRequest(int transactionId, int bookId, String action) {
        this.transactionId = transactionId;
        this.bookId = bookId;
        this.action = action;
    }

    public TransactionActionRequest() {
        transactionId = 0;
        bookId = 0;
        action = "";
    }

    public int getTransactionId() {
        return transactionId;
    }

    public int getBookId() {
        return bookId;
    }

    public String getAction() {
        return action;
    }

}
