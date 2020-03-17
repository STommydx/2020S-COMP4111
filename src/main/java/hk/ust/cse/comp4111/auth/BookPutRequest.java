package hk.ust.cse.comp4111.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BookPutRequest {
    @JsonProperty("Available")
    private boolean avaliable;

    public BookPutRequest() {
    }

    public BookPutRequest(boolean avaliable) {
        this.avaliable = avaliable;
    }

    public boolean isAvaliable() {
        return avaliable;
    }

}
