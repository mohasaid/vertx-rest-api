package com.n26;

import com.n26.entity.Transaction;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class TransactionFactory {

    private TransactionFactory() {
        throw new AssertionError("Ensuring noninstaintability");
    }

    public static Transaction getValidTransaction(final long amount) {
        return new Transaction(BigDecimal.valueOf(amount), ZonedDateTime.now());
    }

    public static Transaction getInvalidTransaction(final long amount) {
        return new Transaction(BigDecimal.valueOf(amount), ZonedDateTime.now().minus(61, ChronoUnit.SECONDS));
    }

    public static Transaction getTransaction(final long amount, final ZonedDateTime timestamp) {
        return new Transaction(BigDecimal.valueOf(amount), timestamp);
    }

}
