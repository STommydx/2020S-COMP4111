package hk.ust.cse.comp4111.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponse {
    @JsonProperty("Token")
    private final String token;

    public LoginResponse(String token) {
        this.token = token;
    }

    public LoginResponse() {
        token = null;
    }

    public String getToken() {
        return token;
    }

}
