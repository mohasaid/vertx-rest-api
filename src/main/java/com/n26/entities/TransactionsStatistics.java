package com.n26.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class TransactionsStatistics implements Serializable {

    private static final long serialVersionUID = -1220119159154051581L;

    private final BigDecimal sum;
    private final BigDecimal avg;
    private final BigDecimal max;
    private final BigDecimal min;
    private final long count;

    public TransactionsStatistics() {
        sum = new BigDecimal(0);
        avg = new BigDecimal(0);
        max = new BigDecimal(0);
        min = new BigDecimal(0);
        count = 0;
    }

    public TransactionsStatistics(BigDecimal sum, BigDecimal avg, BigDecimal max, BigDecimal min, long count) {
        this.sum = sum;
        this.avg = avg;
        this.max = max;
        this.min = min;
        this.count = count;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public BigDecimal getAvg() {
        return avg;
    }

    public BigDecimal getMax() {
        return max;
    }

    public BigDecimal getMin() {
        return min;
    }

    public long getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionsStatistics that = (TransactionsStatistics) o;
        return count == that.count &&
                Objects.equals(sum, that.sum) &&
                Objects.equals(avg, that.avg) &&
                Objects.equals(max, that.max) &&
                Objects.equals(min, that.min);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sum, avg, max, min, count);
    }

    @Override
    public String toString() {
        return "TransactionsStatistics{" +
                "sum=" + sum +
                ", avg=" + avg +
                ", max=" + max +
                ", min=" + min +
                ", count=" + count +
                '}';
    }
}
