package com.moha.utils;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collector;

public class BigDecimalSummaryStatistics implements Consumer<BigDecimal> {

    public static Collector<BigDecimal, ?, BigDecimalSummaryStatistics> getStatistics() {
        return Collector.of(BigDecimalSummaryStatistics::new,
                BigDecimalSummaryStatistics::accept, BigDecimalSummaryStatistics::merge);
    }

    private BigDecimal sum = BigDecimal.ZERO;
    private BigDecimal min = new BigDecimal(Integer.MAX_VALUE);
    private BigDecimal max = new BigDecimal(Integer.MIN_VALUE);
    private long count = 0;

    public void accept(BigDecimal number) {
        if (count == 0) {
            Objects.requireNonNull(number);
            count = 1;
            sum = number;
            min = number;
            max = number;
        } else {
            sum = sum.add(number);
            if (min.compareTo(number) > 0) min = number;
            if (max.compareTo(number) < 0) max = number;
            count++;
        }
    }

    public BigDecimalSummaryStatistics merge(BigDecimalSummaryStatistics summaryStatistics) {
        if (summaryStatistics.count > 0) {
            if (count == 0) {
                count = summaryStatistics.count;
                sum = summaryStatistics.sum;
                min = summaryStatistics.min;
                max = summaryStatistics.max;
            } else {
                sum = sum.add(summaryStatistics.sum);
                if (min.compareTo(summaryStatistics.min) > 0) min = summaryStatistics.min;
                if (max.compareTo(summaryStatistics.max) < 0) max = summaryStatistics.max;
                count += summaryStatistics.count;
            }
        }
        return this;
    }

    public long getCount() {
        return count;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public BigDecimal getAvg() {
        return count < 2 ? sum : sum.divide(BigDecimal.valueOf(count), 2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getMin() {
        return min.equals(new BigDecimal(Integer.MAX_VALUE)) ? new BigDecimal(0) : min;
    }

    public BigDecimal getMax() {
        return max.equals(new BigDecimal(Integer.MIN_VALUE)) ? new BigDecimal(0) : max;
    }
}