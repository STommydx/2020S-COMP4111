package hk.ust.cse.comp4111.book;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BookPutRequest {
    @JsonProperty("Available")
    private boolean available;

    public BookPutRequest() {
    }

    public BookPutRequest(boolean available) {
        this.available = available;
    }

    public boolean isAvailable() {
        return available;
    }

}
