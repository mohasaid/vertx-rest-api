package com.n26.repository.impl;

import com.n26.entity.Transaction;
import com.n26.entity.TransactionsStatistics;
import com.n26.repository.TransactionRepository;
import com.n26.utils.BigDecimalSummaryStatistics;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

@Service
public class TransactionRepositoryImpl implements TransactionRepository {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionRepositoryImpl.class.getName());

    private long timerId;
    private final Vertx vertx;
    private List<Transaction> transactions;
    private TransactionsStatistics transactionsStatistics;
    private static final long TRANSACTIONS_VALID_TIME_IN_SECONDS = 60;
    private static final long ONE_SHOT_TIMER_DELAY_IN_MILLIS = 350;

    @Autowired
    public TransactionRepositoryImpl(final Vertx vertx) {
        this.vertx = vertx;
        this.transactions = new LinkedList<>();
        this.transactionsStatistics = new TransactionsStatistics();
        setUpOneShotTimer();
    }

    @Override
    public synchronized boolean addTransaction(final Transaction transaction) {
        LOG.info("- Adding a transaction - ");
        if (isValidTransaction(transaction)) {
            LOG.info("Valid transaction");
            this.transactions.add(transaction);
            return true;
        }
        LOG.info("Invalid transaction");
        return false;
    }

    @Override
    public synchronized TransactionsStatistics getStatistics() {
        LOG.info("Getting statistics");
        return transactionsStatistics;
    }

    @Override
    public synchronized void deleteTransactions() {
        LOG.info("transactions deleted!");
        this.transactions.clear();
        this.transactionsStatistics = new TransactionsStatistics();
    }

    public void cancelOneShotTimer() {
        vertx.cancelTimer(timerId);
    }

    private void setUpOneShotTimer() {
        timerId = this.vertx.setTimer(ONE_SHOT_TIMER_DELAY_IN_MILLIS, handler -> this.vertx.executeBlocking(future -> {
            removeInvalidTransactions();
            calculateTransactionsStatistics();
            future.complete();
        }, result -> setUpOneShotTimer()));
    }

    private synchronized void calculateTransactionsStatistics() {
        final BigDecimalSummaryStatistics summaryStatistics = this.transactions.parallelStream()
                .filter(this::isValidTransaction)
                .map(Transaction::getAmount)
                .collect(BigDecimalSummaryStatistics.getStatistics());

        this.transactionsStatistics = new TransactionsStatistics(summaryStatistics);
    }

    private synchronized void removeInvalidTransactions() {
        this.transactions.removeIf(transaction -> !isValidTransaction(transaction));
    }

    private boolean isValidTransaction(final Transaction transaction) {
        return ChronoUnit.SECONDS.between(transaction.getTimestamp(), ZonedDateTime.now()) < TRANSACTIONS_VALID_TIME_IN_SECONDS;
    }
}
