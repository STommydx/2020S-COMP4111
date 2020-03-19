package hk.ust.cse.comp4111.book;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.ListIterator;

public class BookSearchResponse {
    @JsonProperty("FoundBooks")
    private final int totalNumBooks;

    @JsonProperty("Results")
    private final List<AddBookRequest> searchResults;

    public BookSearchResponse(){
        totalNumBooks = 0;
        searchResults = null;
    }

    private BookSearchResponse(int totalNumBooks, List<AddBookRequest> searchResults){
        this.totalNumBooks = totalNumBooks;
        this.searchResults = searchResults;
    }

    public int getTotalNumBooks() {
        return totalNumBooks;
    }

    public List<AddBookRequest> getSearchResults() {
        return searchResults;
    }

    public static class Builder {
        private List<AddBookRequest> list;

        public Builder(){
            list = new ArrayList<>();
        }

        public Builder addBook(AddBookRequest book){
            list.add(book);
            return this;
        }

        public BookSearchResponse build(){
            return new BookSearchResponse(list.size(),list);
        }


    }


}
