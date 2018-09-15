package com.n26.utils;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class RoundUtilsTest {

    @Test
    public void givenNumberWithMoreThanTwoDecimals_WhenRoundHalfUp_ThenReturnsTwoDecimalsRoundingByHalfUp() {
        final BigDecimal numberToRound = new BigDecimal("10.345");
        final BigDecimal expectedResult = new BigDecimal("10.35");

        final BigDecimal rounded = RoundUtils.roundHalfUp(numberToRound);

        Assert.assertEquals(expectedResult, rounded);
    }

    @Test
    public void givenNumberWithTwoDecimals_WhenRoundHalfUp_ThenReturnsTheSameNumber() {
        final BigDecimal numberToRound = new BigDecimal("10.80");
        final BigDecimal expectedResult = new BigDecimal("10.80");

        final BigDecimal rounded = RoundUtils.roundHalfUp(numberToRound);

        Assert.assertEquals(expectedResult, rounded);
    }
}
