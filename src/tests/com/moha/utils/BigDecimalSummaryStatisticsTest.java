package com.moha.utils;

import com.moha.entity.Transaction;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static com.moha.TransactionFactory.getValidTransaction;
import static com.moha.utils.RoundUtils.roundHalfUp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BigDecimalSummaryStatisticsTest {

    @Test
    public void givenListOfBigDecimals_whenGetStatistics_ReturnsCorrectStatistics() {
        final List<BigDecimal> decimalList = Arrays.asList(
                BigDecimal.valueOf(1),
                BigDecimal.valueOf(2),
                BigDecimal.valueOf(3));

        final BigDecimalSummaryStatistics statistics = decimalList.parallelStream()
                .collect(BigDecimalSummaryStatistics.getStatistics());

        assertNotNull(statistics);
        assertEquals(decimalList.size(), statistics.getCount());
        assertEquals(BigDecimal.valueOf(1), statistics.getMin());
        assertEquals(BigDecimal.valueOf(3), statistics.getMax());
        assertEquals(roundHalfUp(BigDecimal.valueOf(2)), statistics.getAvg());
        assertEquals(BigDecimal.valueOf(6), statistics.getSum());
    }

    @Test
    public void givenListOfTransactions_whenGetStatistics_ReturnCorrectStatistics() {
        final List<Transaction> transactionList = Arrays.asList(
                getValidTransaction(1),
                getValidTransaction(2),
                getValidTransaction(3));

        final BigDecimalSummaryStatistics statistics = transactionList.parallelStream()
                .map(Transaction::getAmount)
                .collect(BigDecimalSummaryStatistics.getStatistics());

        assertNotNull(statistics);
        assertEquals(transactionList.size(), statistics.getCount());
        assertEquals(BigDecimal.valueOf(1), statistics.getMin());
        assertEquals(BigDecimal.valueOf(3), statistics.getMax());
        assertEquals(roundHalfUp(BigDecimal.valueOf(2)), statistics.getAvg());
        assertEquals(BigDecimal.valueOf(6), statistics.getSum());
    }
}
