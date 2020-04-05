package hk.ust.cse.comp4111.transaction;

import hk.ust.cse.comp4111.exception.BadTransactionActionException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Transaction {
    private static int nextAvailableId = 0;
    private final int id;
    private final List<TransactionAction> actions;

    public Transaction() {
        synchronized (Transaction.class) {
            id = nextAvailableId++;
        }
        actions = Collections.synchronizedList(new ArrayList<>());
    }

    public int getId() {
        return id;
    }

    public List<TransactionAction> getActions() {
        return actions;
    }

    public boolean addAction(TransactionActionRequest request) throws BadTransactionActionException {
        return actions.add(new TransactionAction(request));
    }

    public static class TransactionAction {
        private final int bookId;
        private final boolean available;

        public TransactionAction(int bookId, boolean available) {
            this.bookId = bookId;
            this.available = available;
        }

        public TransactionAction(@NotNull TransactionActionRequest request) throws BadTransactionActionException {
            this.bookId = request.getBookId();
            if (request.getAction().equals("loan")) {
                this.available = false;
            } else if (request.getAction().equals("return")) {
                this.available = true;
            } else {
                throw new BadTransactionActionException(request.getAction());
            }
        }

        public int getBookId() {
            return bookId;
        }

        public boolean isAvailable() {
            return available;
        }

    }
}
