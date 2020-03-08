package hk.ust.cse.comp4111.exception;

public class BadCommitException extends Exception {
    private final int transactionId;

    public BadCommitException(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getTransactionId() {
        return transactionId;
    }

}
