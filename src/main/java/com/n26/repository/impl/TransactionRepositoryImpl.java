package com.n26.repository.impl;

import com.n26.entity.Transaction;
import com.n26.entity.TransactionsStatistics;
import com.n26.repository.TransactionRepository;

public class TransactionRepositoryImpl implements TransactionRepository {

    @Override
    public boolean addTransaction(Transaction transaction) {
        return false;
    }

    @Override
    public TransactionsStatistics getStatistics() {
        return null;
    }

    @Override
    public void deleteTransactions() {

    }

}
