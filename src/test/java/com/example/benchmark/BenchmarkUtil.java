package com.example.benchmark;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.math.BigInteger.valueOf;
import static org.junit.Assert.assertTrue;

/**
 *
 */
class BenchmarkUtil {
    private static Log log = LogFactory.getLog(BenchmarkUtil.class);

    public static void executeConcurrent(String testName,
                                         Runnable runnable,
                                         int threadCount,
                                         int maxTimeoutSeconds) throws InterruptedException {

        List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<Throwable>());
        ExecutorService threadPool = Executors.newFixedThreadPool(threadCount);
        try {
            CountDownLatch allExecutorThreadsReady = new CountDownLatch(threadCount);
            CountDownLatch afterInitBlocker = new CountDownLatch(1);
            CountDownLatch allDone = new CountDownLatch(threadCount);

            IntStream.range(0, threadCount).forEach(
                    idx -> threadPool.submit(
                            () -> {
                                allExecutorThreadsReady.countDown();
                                try {
                                    afterInitBlocker.await();

                                    runnable.run();

                                } catch (Throwable e) {
                                    exceptions.add(e);
                                } finally {
                                    allDone.countDown();
                                }
                            }));

            // wait until all threads are ready
            assertTrue("Timeout initializing threads! Perform long lasting " +
                            "initializations before passing runnables to executeConcurrent",
                    allExecutorThreadsReady.await(threadCount * 10, TimeUnit.MILLISECONDS));
            // start all test runners
            afterInitBlocker.countDown();
            log.info("Test " + testName + " started in " + threadCount + " threads");

            assertTrue(testName + " timeout! More than" + maxTimeoutSeconds + "seconds",
                    allDone.await(maxTimeoutSeconds, TimeUnit.SECONDS));
        } finally {
            threadPool.shutdownNow();
        }
        assertTrue(testName + " failed with exception(s): " + exceptions, exceptions.isEmpty());
        log.info("Concurrent test " + testName + " finished");
    }

    static <T, R> void startTest(String testName, List<T> dataList, Function<T, R> operation, int executionsCount) {
        int packetSize = dataList.size();
        int loggingPeriod = 20;

        StopWatch totalSw = new StopWatch();
        totalSw.start();

        StopWatch localSw = new StopWatch();
        localSw.start();

        int iterations = executionsCount / packetSize;
        for (int i = 0; i < iterations; i++) {

            dataList.stream()
                    .map(operation).collect(Collectors.toList());

            if (i % loggingPeriod == 0 && i > 0) {
                localSw.stop();

                long duration = localSw.getTime(TimeUnit.SECONDS);
                BigInteger speed = valueOf(loggingPeriod * packetSize).divide(valueOf(duration));
                log.info("Test " + testName + " intermediate speed for " + loggingPeriod + " operations: " + speed + " op/s");

                localSw.reset();
                localSw.start();
            }
        }

        totalSw.stop();

        long totalDuration = totalSw.getTime(TimeUnit.SECONDS);
        BigInteger totalSpeed = valueOf(executionsCount).divide(valueOf(totalDuration));

        log.info("Test " + testName + " completed for " + executionsCount +
                " operations, speed: " + totalSpeed + " op/s");
    }
}
