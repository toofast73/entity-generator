package com.example.benchmark;

import com.example.Start;
import com.example.dao.ReaderService;
import com.example.dao.WriterService;
import com.example.data.filereader.JsonLoader;
import com.example.data.filereader.KeyValueLoader;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.math.BigInteger.valueOf;

/**
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Start.class)
//@Transactional  // с анннотацией данные в БД откатываются после прогона
public class WriteBenchmark {
    private static final int OPERATIONS_COUNT = 1000;
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private WriterService writerService;
    @Autowired
    private ReaderService readerService;
    @Autowired
    private JsonLoader jsonLoader;
    @Autowired
    private KeyValueLoader keyValueLoader;

    @Test
    public void testKeyValue() throws Exception {

        List<Map<String, String>> initialOperationsData = keyValueLoader.load_1_20f();

        testWrite("write in key value", initialOperationsData,
                operationData -> writerService.createKeyValueOperation(operationData), OPERATIONS_COUNT
        );
    }

    @Test
    public void testChunks() throws Exception {

        List<String> initialOperationsData = jsonLoader.load_1_20f();

        testWrite("write in chunks", initialOperationsData,
                operationData -> writerService.createChunkedOperation(operationData), OPERATIONS_COUNT
        );
    }

    private <T> void testWrite(String testName, List<T> initialOperationsData,
                               Function<T, Long> writeOperation, int operationsCount) {

        int packetSize = initialOperationsData.size();
        int loggingPeriod = 20;

        StopWatch totalSw = new StopWatch();
        totalSw.start();

        StopWatch localSw = new StopWatch();
        localSw.start();

        int iterations = operationsCount / packetSize;
        for (int i = 0; i < iterations; i++) {

            initialOperationsData.stream()
                    .map(writeOperation).collect(Collectors.toList());

            if (i % loggingPeriod == 0 && i > 0) {
                localSw.stop();

                long duration = localSw.getTime(TimeUnit.SECONDS);
                BigInteger speed = valueOf(loggingPeriod * packetSize).divide(valueOf(duration));
                log.info("Test " + testName + " intermediate speed " + speed + " op/s");

                localSw.reset();
                localSw.start();
            }
        }

        totalSw.stop();

        long totalDuration = totalSw.getTime(TimeUnit.SECONDS);
        BigInteger totalSpeed = valueOf(operationsCount).divide(valueOf(totalDuration));

        log.info("Test " + testName + " completed for " + operationsCount +
                " operations, speed: " + totalSpeed + " op/s");
    }
}
