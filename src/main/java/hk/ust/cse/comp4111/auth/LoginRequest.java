package hk.ust.cse.comp4111.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginRequest {
    @JsonProperty("Username")
    private String username;
    @JsonProperty("Password")
    private String password;

    public LoginRequest() {
        username = "";
        password = "";
    }

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
