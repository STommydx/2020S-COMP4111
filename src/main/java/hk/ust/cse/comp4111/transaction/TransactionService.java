package hk.ust.cse.comp4111.transaction;

import hk.ust.cse.comp4111.exception.BadTransactionActionException;
import hk.ust.cse.comp4111.exception.BadTransactionIdException;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionService {

    private static Map<UUID, TransactionService> transactionServiceMap = new ConcurrentHashMap<>();
    private Map<Integer, Transaction> transactionMap;

    private TransactionService() {
        transactionMap = new ConcurrentHashMap<>();
    }

    public static TransactionService getInstance(UUID user) {
        transactionServiceMap.putIfAbsent(user, new TransactionService());
        return transactionServiceMap.get(user);
    }

    public Transaction newTransaction() {
        Transaction transaction = new Transaction();
        transactionMap.put(transaction.getId(), transaction);
        return transaction;
    }

    public boolean addTransactionAction(TransactionActionRequest request) throws BadTransactionIdException, BadTransactionActionException {
        Transaction transaction = transactionMap.getOrDefault(request.getTransactionId(), null);
        if (transaction == null) {
            throw new BadTransactionIdException(request.getTransactionId());
        }
        return transaction.addAction(request);
    }


}
