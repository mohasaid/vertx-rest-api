package com.moha;

import java.util.concurrent.TimeUnit;

public class TestUtils {

    private TestUtils() {
        throw new AssertionError("Ensuring noninstaintability");
    }

    public static final double DELTA = 0.001;

    public static void sleepUntilStatisticsAreGenerated() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(400);
    }

}
