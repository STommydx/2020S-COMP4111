package hk.ust.cse.comp4111.exception;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;

public class BookExistException extends Exception {
    private int id;
    public BookExistException(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
