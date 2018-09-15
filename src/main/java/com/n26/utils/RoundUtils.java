package com.n26.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RoundUtils {

    private RoundUtils() {
        throw new AssertionError("Ensuring noninstaintability");
    }

    public static BigDecimal roundHalfUp(final BigDecimal number) {
        return number.setScale(2, RoundingMode.HALF_UP);
    }

}
