package com.n26.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
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
}
