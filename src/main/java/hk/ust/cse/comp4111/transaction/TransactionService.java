package hk.ust.cse.comp4111.transaction;

import hk.ust.cse.comp4111.database.ConnectionManager;
import hk.ust.cse.comp4111.database.DatabaseTransaction;
import hk.ust.cse.comp4111.exception.BadCommitException;
import hk.ust.cse.comp4111.exception.BadTransactionActionException;
import hk.ust.cse.comp4111.exception.BadTransactionIdException;
import hk.ust.cse.comp4111.exception.InternalServerException;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionService {

    private static Map<UUID, TransactionService> transactionServiceMap = new ConcurrentHashMap<>();
    private Map<Integer, Transaction> transactionMap;

    private TransactionService() {
        transactionMap = new ConcurrentHashMap<>();
    }

    public static TransactionService getInstance(@NotNull UUID user) {
        transactionServiceMap.putIfAbsent(user, new TransactionService());
        return transactionServiceMap.get(user);
    }

    public Transaction newTransaction() {
        Transaction transaction = new Transaction();
        transactionMap.put(transaction.getId(), transaction);
        return transaction;
    }

    public boolean addTransactionAction(@NotNull TransactionActionRequest request) throws BadTransactionIdException, BadTransactionActionException {
        Transaction transaction = transactionMap.getOrDefault(request.getTransactionId(), null);
        if (transaction == null) {
            throw new BadTransactionIdException(request.getTransactionId());
        }
        return transaction.addAction(request);
    }

    public void cancelTransaction(int id) throws BadTransactionIdException {
        Transaction transaction = transactionMap.remove(id);
        if (transaction == null) throw new BadTransactionIdException(id);
    }

    public void commitTransaction(int id) throws InternalServerException, BadTransactionIdException, BadCommitException {
        Transaction transaction = transactionMap.getOrDefault(id, null);
        if (transaction == null) {
            throw new BadTransactionIdException(id);
        }
        try (Connection connection = ConnectionManager.getConnection()) {
            boolean result = DatabaseTransaction.commit(connection, transaction.getActions());
            if (!result) throw new BadCommitException(id);
        } catch (SQLException e) {
            throw new InternalServerException(e);
        }
    }


}
