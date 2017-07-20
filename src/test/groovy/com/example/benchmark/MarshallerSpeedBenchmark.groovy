package com.example.benchmark

import com.example.Start
import com.example.data.converter.PojoConverter
import com.example.data.filereader.JsonLoader
import com.example.data.filereader.KeyValueLoader
import com.example.data.filereader.PojoLoader
import com.example.data.pojo.Operation
import groovy.transform.CompileStatic
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import java.util.concurrent.Callable

/**
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Start.class)
@CompileStatic
class MarshallerSpeedBenchmark {

    @Autowired
    private JsonLoader jsonLoader
    @Autowired
    private KeyValueLoader keyValueLoader
    @Autowired
    private PojoLoader pojoLoader
    @Autowired
    private PojoConverter converter

    @Test
    void testPojoToJson() {

        [20, 100, 500, 10_000].each { fieldsCount ->

            List<Operation[]> pojos = pojoLoader.load(fieldsCount)
            BenchmarkSuite.executeBenchmark(prepareReport(),
                    [("Marshal POJO to JSON, $fieldsCount fields" as String): {
                        pojos.collect {
                            pojo -> converter.convertPojoToJson(pojo)
                        }
                    } as Callable])
        }
    }

    @Test
    void testJsonToPojo() {

        [20, 100, 500, 10_000].each { fieldsCount ->

            List<String> jsons = jsonLoader.load(fieldsCount)
            BenchmarkSuite.executeBenchmark(prepareReport(),
                    [("Marshal JSON to POJO, $fieldsCount fields" as String): {
                        jsons.collect {
                            json -> converter.convertJsonToPojo(json)
                        }
                    } as Callable])
        }
    }

    @Test
    void testPojoToKeyValue() {

        [20, 100, 500, 10_000].each { fieldsCount ->

            List<Operation[]> pojos = pojoLoader.load(fieldsCount)
            BenchmarkSuite.executeBenchmark(prepareReport(),
                    [("Marshal POJO to KeyValue, $fieldsCount fields" as String): {
                        pojos.collect {
                            pojo -> converter.convertPojoToKeyValue(pojo)
                        }
                    } as Callable])
        }
    }

    @Test
    void testKeyValueToPojo() {

        [20, 100, 500, 10_000].each { fieldsCount ->

            List<Map<String, String>> keyValues = keyValueLoader.load(fieldsCount)
            BenchmarkSuite.executeBenchmark(prepareReport(),
                    [("Marshal KeyValue to POJO, $fieldsCount fields" as String): {
                        keyValues.collect {
                            keyValue -> converter.convertKeyValueToPojo(keyValue)
                        }
                    } as Callable])
        }
    }

    private static BenchmarkReport prepareReport() {
        new BenchmarkReport(
                duration: 10_000,
                threadCount: 1,
                checkSpeedInterval: 10_000,
                logInIntervalsEnabled: false)
    }
}
