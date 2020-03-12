package hk.ust.cse.comp4111.exception;

public class BadTransactionActionException extends Exception {
    private final String action;

    public BadTransactionActionException(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

}
