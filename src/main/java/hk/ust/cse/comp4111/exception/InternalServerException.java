package hk.ust.cse.comp4111.exception;

public class InternalServerException extends Exception {
    public InternalServerException(Exception exception) {
        super(exception);
    }
}
