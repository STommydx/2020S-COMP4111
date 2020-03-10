package hk.ust.cse.comp4111.exception;

public class BadCredentialsException extends Exception {
    private final String username;

    public BadCredentialsException(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
