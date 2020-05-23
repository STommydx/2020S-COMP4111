package hk.ust.cse.comp4111.transaction;

import hk.ust.cse.comp4111.database.DatabaseTransaction;
import hk.ust.cse.comp4111.exception.*;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionService {

    private static final Map<UUID, TransactionService> transactionServiceMap = new ConcurrentHashMap<>();
    private final Map<Integer, Transaction> transactionMap;

    private TransactionService() {
        transactionMap = new ConcurrentHashMap<>();
    }

    @NotNull
    public static TransactionService getInstance(@NotNull UUID user) {
        transactionServiceMap.putIfAbsent(user, new TransactionService());
        return transactionServiceMap.get(user);
    }

    public Transaction newTransaction() throws NoAvailableTransactionException {
        Transaction transaction;
        try {
            transaction = new Transaction();
        } catch (SQLException e) {
            throw new NoAvailableTransactionException();
        }
        transactionMap.put(transaction.getId(), transaction);
        return transaction;
    }

    public boolean addTransactionAction(@NotNull TransactionActionRequest request) throws BadTransactionIdException, BadTransactionActionException, InternalServerException {
        Transaction transaction = transactionMap.get(request.getTransactionId());
        if (transaction == null) {
            throw new BadTransactionIdException(request.getTransactionId());
        }
        try {
            return DatabaseTransaction.update(transaction, new Transaction.TransactionAction(request));
        } catch (SQLException e) {
            throw new InternalServerException(e);
        }
    }

    public void cancelTransaction(int id) throws BadTransactionIdException, InternalServerException {
        try (Transaction transaction = transactionMap.remove(id)) {
            if (transaction == null) {
                throw new BadTransactionIdException(id);
            }
            DatabaseTransaction.cancel(transaction);
        } catch (SQLException e) {
            throw new InternalServerException(e);
        }
    }

    public void commitTransaction(int id) throws InternalServerException, BadTransactionIdException, BadCommitException {
        try (Transaction transaction = transactionMap.remove(id)) {
            if (transaction == null) {
                throw new BadTransactionIdException(id);
            }
            boolean result = DatabaseTransaction.commit(transaction);
            if (!result) throw new BadCommitException(id);
        } catch (SQLException e) {
            throw new InternalServerException(e);
        }
    }


}
