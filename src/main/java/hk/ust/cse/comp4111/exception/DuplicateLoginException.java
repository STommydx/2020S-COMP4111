package hk.ust.cse.comp4111.exception;

public class DuplicateLoginException extends Exception {
    private final String username;

    public DuplicateLoginException(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
