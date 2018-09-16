package com.moha.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.moha.utils.BigDecimalSummaryStatistics;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static com.moha.utils.RoundUtils.roundHalfUp;

@Getter
@EqualsAndHashCode
@ToString
public class TransactionsStatistics implements Serializable {

    private static final long serialVersionUID = -1220119159154051581L;

    @JsonFormat(shape = STRING)
    private final BigDecimal sum;
    @JsonFormat(shape = STRING)
    private final BigDecimal avg;
    @JsonFormat(shape = STRING)
    private final BigDecimal max;
    @JsonFormat(shape = STRING)
    private final BigDecimal min;
    private final long count;

    public TransactionsStatistics() {
        sum = roundHalfUp(new BigDecimal(0));
        avg = roundHalfUp(new BigDecimal(0));
        max = roundHalfUp(new BigDecimal(0));
        min = roundHalfUp(new BigDecimal(0));
        count = 0;
    }

    public TransactionsStatistics(BigDecimalSummaryStatistics summaryStatistics) {
        this.avg = roundHalfUp(summaryStatistics.getAvg());
        this.sum = roundHalfUp(summaryStatistics.getSum());
        this.min = roundHalfUp(summaryStatistics.getMin());
        this.max = roundHalfUp(summaryStatistics.getMax());
        this.count = summaryStatistics.getCount();
    }
}
