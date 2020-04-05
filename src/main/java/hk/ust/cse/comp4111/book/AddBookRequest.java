package hk.ust.cse.comp4111.book;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddBookRequest {
    @JsonProperty("Title")
    private final String title;

    @JsonProperty("Author")
    private final String author;

    @JsonProperty("Publisher")
    private final String publisher;

    @JsonProperty("Year")
    private int year;

    public AddBookRequest() {
        title = "";
        author = "";
        publisher = "";
    }

    public AddBookRequest(String title, String author, String publisher, int year) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublisher() {
        return publisher;
    }

    public int getYear() {
        return year;
    }
}
