package com.example.benchmark

import com.example.Start
import com.example.data.converter.JacksonConverter
import com.example.data.converter.PojoToKeyValueConverter
import com.example.data.filereader.JsonLoader
import com.example.data.filereader.KeyValueLoader
import com.example.data.filereader.PojoLoader
import com.example.data.pojo.Operation
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
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
class MarshallerSpeedBenchmark {
    private static Log log = LogFactory.getLog(MarshallerSpeedBenchmark.class)

    @Autowired
    private JsonLoader jsonLoader
    @Autowired
    private KeyValueLoader keyValueLoader
    @Autowired
    private PojoLoader pojoLoader
    @Autowired
    private JacksonConverter jacksonConverter
    @Autowired
    private PojoToKeyValueConverter pojoToKeyValueConverter

    @Test
    void testPojoToJson() {

        [20, 100, 500, 10_000].each { fieldsCount ->

            List<Operation[]> pojos = pojoLoader.load(fieldsCount)
            BenchmarkSuite.executeBenchmark(prepareReport(),
                    [("Marshal POJO to JSON, $fieldsCount fields" as String): {
                        pojos.collect {
                            pojo -> jacksonConverter.toJson(pojo)
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
                            json -> jacksonConverter.fromJson(json, Operation[].class)
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
                            pojo -> pojoToKeyValueConverter.convertTo(pojo)
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
                            keyValue -> pojoToKeyValueConverter.convertFrom(keyValue)
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
