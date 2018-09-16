package com.moha.repository;

import com.moha.entity.Transaction;
import com.moha.entity.TransactionsStatistics;
import com.moha.repository.impl.TransactionRepositoryImpl;
import io.vertx.core.Vertx;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.LongStream;

import static com.moha.TestUtils.DELTA;
import static com.moha.TestUtils.sleepUntilStatisticsAreGenerated;
import static com.moha.TransactionFactory.*;
import static com.moha.utils.RoundUtils.roundHalfUp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TransactionRepositoryImplTest {

    private TransactionRepositoryImpl transactionRepository;

    @Before
    public void setUp() {
        transactionRepository = new TransactionRepositoryImpl(Vertx.vertx());
    }

    @After
    public void cleanUp() {
        transactionRepository.cancelOneShotTimer();
    }

    @Test
    public void givenValidTransaction_whenAddTransaction_thenIsStored() throws InterruptedException {
        transactionRepository.addTransaction(getValidTransaction(10));

        sleepUntilStatisticsAreGenerated();

        final TransactionsStatistics transactionsStatistics = transactionRepository.getStatistics();
        assertNotNull(transactionsStatistics);
        assertEquals(1, transactionsStatistics.getCount(), DELTA);
    }

    @Test
    public void givenInvalidTransaction_whenaAddTransaction_thenIsNotStored() throws InterruptedException {
        transactionRepository.addTransaction(getInvalidTransaction(10));

        sleepUntilStatisticsAreGenerated();

        final TransactionsStatistics transactionsStatistics = transactionRepository.getStatistics();
        assertNotNull(transactionsStatistics);
        assertEquals(0, transactionsStatistics.getCount(), DELTA);
    }

    @Test
    public void givenValidTransactions_whenAddTransaction_thenReturnsValidStatistics() throws InterruptedException {
        transactionRepository.addTransaction(getValidTransaction(10));
        transactionRepository.addTransaction(getValidTransaction(20));
        transactionRepository.addTransaction(getValidTransaction(30));

        sleepUntilStatisticsAreGenerated();

        final TransactionsStatistics transactionsStatistics = transactionRepository.getStatistics();
        assertNotNull(transactionsStatistics);
        assertEquals(3, transactionsStatistics.getCount(), DELTA);
        assertEquals(roundHalfUp(BigDecimal.valueOf(10)), transactionsStatistics.getMin());
        assertEquals(roundHalfUp(BigDecimal.valueOf(20)), transactionsStatistics.getAvg());
        assertEquals(roundHalfUp(BigDecimal.valueOf(30)), transactionsStatistics.getMax());
        assertEquals(roundHalfUp(BigDecimal.valueOf(60)), transactionsStatistics.getSum());
    }

    @Test
    public void given100ValidTransactionsConcurrently_whenAddTransactions_thenReturnsValidStatistics() throws InterruptedException {
        final ZonedDateTime time = ZonedDateTime.now();
        LongStream.range(1, 101).forEach(i -> {
            ZonedDateTime dateTime = time.minus(i * 100, ChronoUnit.MILLIS);
            Transaction transaction = getTransaction(i, dateTime);
            transactionRepository.addTransaction(transaction);
        });

        sleepUntilStatisticsAreGenerated();

        final TransactionsStatistics transactionsStatistics = transactionRepository.getStatistics();
        assertNotNull(transactionsStatistics);
        assertEquals(100, transactionsStatistics.getCount(), DELTA);
        assertEquals(roundHalfUp(BigDecimal.valueOf(1)), transactionsStatistics.getMin());
        assertEquals(roundHalfUp(BigDecimal.valueOf(50.50)), transactionsStatistics.getAvg());
        assertEquals(roundHalfUp(BigDecimal.valueOf(100.00)), transactionsStatistics.getMax());
        assertEquals(roundHalfUp(BigDecimal.valueOf(5050.00)), transactionsStatistics.getSum());
    }

    @Test
    public void givenValidTransactions_whenDeleteTransactions_ThenReturnsDefaultStatistics() throws InterruptedException {
        transactionRepository.addTransaction(getValidTransaction(10));
        transactionRepository.addTransaction(getValidTransaction(20));
        transactionRepository.addTransaction(getValidTransaction(30));

        sleepUntilStatisticsAreGenerated();

        final TransactionsStatistics transactionsStatistics = transactionRepository.getStatistics();
        assertNotNull(transactionsStatistics);
        assertEquals(3, transactionsStatistics.getCount(), DELTA);
        assertEquals(roundHalfUp(BigDecimal.valueOf(10)), transactionsStatistics.getMin());
        assertEquals(roundHalfUp(BigDecimal.valueOf(20)), transactionsStatistics.getAvg());
        assertEquals(roundHalfUp(BigDecimal.valueOf(30)), transactionsStatistics.getMax());
        assertEquals(roundHalfUp(BigDecimal.valueOf(60)), transactionsStatistics.getSum());

        transactionRepository.deleteTransactions();

        final TransactionsStatistics updatedStatistics = transactionRepository.getStatistics();
        assertNotNull(updatedStatistics);
        assertEquals(0, updatedStatistics.getCount(), DELTA);
        assertEquals(roundHalfUp(BigDecimal.valueOf(0)), updatedStatistics.getMin());
        assertEquals(roundHalfUp(BigDecimal.valueOf(0)), updatedStatistics.getAvg());
        assertEquals(roundHalfUp(BigDecimal.valueOf(0)), updatedStatistics.getMax());
        assertEquals(roundHalfUp(BigDecimal.valueOf(0)), updatedStatistics.getSum());
    }
}
