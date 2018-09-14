package com.n26.repository;

import com.n26.entity.Transaction;
import com.n26.entity.TransactionsStatistics;

public interface TransactionRepository {

    boolean addTransaction(Transaction transaction);

    TransactionsStatistics getStatistics();

    void deleteTransactions();
}
