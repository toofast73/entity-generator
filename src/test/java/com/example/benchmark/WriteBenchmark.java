package com.example.benchmark;

import com.example.Start;
import com.example.dao.WriterService;
import com.example.data.filereader.JsonLoader;
import com.example.data.filereader.KeyValueLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

import static com.example.benchmark.BenchmarkUtil.executeConcurrent;
import static com.example.benchmark.BenchmarkUtil.startTest;

/**
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Start.class)
//@Transactional  // с анннотацией данные в БД откатываются после прогона
public class WriteBenchmark {
    private static Log log = LogFactory.getLog(WriteBenchmark.class);

    private static final int THREAD_COUNT = 4;
    private static int OPERATIONS_COUNT = 300;
    private static final int LOGGING_PERIOD = 100;
    private static final int MAX_TIMEOUT_IN_SECONDS = 3_600;

    @Autowired
    private WriterService writerService;
    @Autowired
    private JsonLoader jsonLoader;
    @Autowired
    private KeyValueLoader keyValueLoader;

    @Test
    public void testKeyValue() throws Exception {

        List<Map<String, String>> operations = keyValueLoader.load_1_10000f();

        executeConcurrent("write in key value",
                () -> startTest("write in key value", operations,
                        operationData -> writerService.createKeyValueOperation(operationData),
                        OPERATIONS_COUNT, LOGGING_PERIOD),
                THREAD_COUNT, MAX_TIMEOUT_IN_SECONDS);
    }

    @Test
    public void testChunks() throws Exception {

        List<String> operations = jsonLoader.load_1_10000f();

        executeConcurrent("write in chunks",
                () -> startTest("write in chunks", operations,
                        operationData -> writerService.createChunkedOperation(operationData),
                        OPERATIONS_COUNT, LOGGING_PERIOD),
                THREAD_COUNT, MAX_TIMEOUT_IN_SECONDS);
    }
}
