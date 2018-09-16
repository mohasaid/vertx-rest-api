package com.moha.repository;

import com.moha.entity.Transaction;
import com.moha.entity.TransactionsStatistics;

public interface TransactionRepository {

    boolean addTransaction(Transaction transaction);

    TransactionsStatistics getStatistics();

    void deleteTransactions();
}
