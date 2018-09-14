package com.n26.repository.impl;

import com.n26.entity.Transaction;
import com.n26.entity.TransactionsStatistics;
import com.n26.repository.TransactionRepository;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TransactionRepositoryImpl implements TransactionRepository {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionRepositoryImpl.class.getName());

    @Override
    public synchronized boolean addTransaction(Transaction transaction) {
        LOG.info("transaction added!");
        return false;
    }

    @Override
    public synchronized TransactionsStatistics getStatistics() {
        LOG.info("statistics returned!");
        return new TransactionsStatistics();
    }

    @Override
    public synchronized void deleteTransactions() {
        LOG.info("transactions deleted!");
    }

}
