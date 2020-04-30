package hk.ust.cse.comp4111.book;

public class BookSearchRequest {
    private boolean searchById = false;
    private boolean searchByTitle = false;
    private boolean searchByAuthor = false;
    private boolean searchByPublisher = false;
    private boolean searchByYear = false;
    private boolean sorted = false;
    private boolean sortReversed = false;
    private boolean limited = false;

    private int id;
    private String author;

    private String publisher;
    private String title;
    private int year;
    private int limit;
    private SortType sortType;

    private BookSearchRequest() {
        author = "";
        title = "";
        publisher = "";
        limit = Integer.MAX_VALUE;
        sortType = SortType.BY_TITLE;
    }

    public boolean isSearchById() {
        return searchById;
    }

    public boolean isSearchByTitle() {
        return searchByTitle;
    }

    public boolean isSearchByAuthor() {
        return searchByAuthor;
    }

    public boolean isSorted() {
        return sorted;
    }

    public boolean isSortReversed() {
        return sortReversed;
    }

    public boolean isLimited() {
        return limited;
    }

    public int getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public int getLimit() {
        return limit;
    }

    public SortType getSortType() {
        return sortType;
    }

    public boolean isSearchByPublisher() {
        return searchByPublisher;
    }

    public boolean isSearchByYear() {
        return searchByYear;
    }

    public String getPublisher() {
        return publisher;
    }

    public int getYear() {
        return year;
    }


    public enum SortType {
        BY_ID,
        BY_TITLE,
        BY_AUTHOR,
        BY_PUBLISHER,
        BY_YEAR
    }

    public static class Builder {
        final BookSearchRequest instance;

        public Builder() {
            instance = new BookSearchRequest();
        }

        public BookSearchRequest build() {
            return instance;
        }

        public Builder id(int id) {
            instance.searchById = true;
            instance.id = id;
            return this;
        }

        public Builder title(String title) {
            instance.searchByTitle = true;
            instance.title = title;
            return this;
        }

        public Builder author(String author) {
            instance.searchByAuthor = true;
            instance.author = author;
            return this;
        }

        public Builder publisher(String publisher) {
            instance.searchByPublisher = true;
            instance.publisher = publisher;
            return this;
        }

        public Builder year(int year) {
            instance.searchByYear = true;
            instance.year = year;
            return this;
        }

        public Builder sortById() {
            instance.sorted = true;
            instance.sortType = SortType.BY_ID;
            return this;
        }

        public Builder sortByTitle() {
            instance.sorted = true;
            instance.sortType = SortType.BY_TITLE;
            return this;
        }

        public Builder sortByAuthor() {
            instance.sorted = true;
            instance.sortType = SortType.BY_AUTHOR;
            return this;
        }

        public Builder sortByPublisher() {
            instance.sorted = true;
            instance.sortType = SortType.BY_PUBLISHER;
            return this;
        }

        public Builder sortByYear() {
            instance.sorted = true;
            instance.sortType = SortType.BY_YEAR;
            return this;
        }

        public Builder reverseSort() {
            instance.sortReversed = true;
            return this;
        }

        public Builder limit(int limit) {
            instance.limited = true;
            instance.limit = limit;
            return this;
        }
    }
}
