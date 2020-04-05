package hk.ust.cse.comp4111.exception;

public class TokenNotFoundException extends Exception {
    private final String token;

    public TokenNotFoundException(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
