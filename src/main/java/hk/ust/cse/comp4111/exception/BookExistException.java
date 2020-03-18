package hk.ust.cse.comp4111.exception;

public class BookExistException extends Exception {
    private int id;

    public BookExistException(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
