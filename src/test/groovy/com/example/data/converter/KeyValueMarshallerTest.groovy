package com.example.data.converter

import com.example.Start
import com.example.data.filereader.JsonLoader
import com.example.data.filereader.KeyValueLoader
import com.example.data.pojo.Operation
import groovy.json.JsonOutput
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import static org.junit.Assert.assertEquals

/**
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Start.class)
@CompileStatic
@Slf4j
class KeyValueMarshallerTest {

    @Autowired
    KeyValueMarshaller jsonToKeyValueConverter
    @Autowired
    JsonLoader jsonLoader
    @Autowired
    KeyValueLoader keyValueLoader
    @Autowired
    JacksonMarshaller jacksonConverter

    @Test
    void testPojoToKeyValue() throws Exception {

        jsonLoader.loadAll().each { json ->

            Map<String, String> map = jsonToKeyValueConverter.toKeyValue(json)
            StringBuilder sb = new StringBuilder("Result map:")

            map.each { key, value ->
                sb.append(key)
                        .append('=')
                        .append(value)
                        .append('\n')
            }

            log.info(sb.toString())
        }
    }

    @Test
    void testKeyValueToPojo() throws Exception {

        [20, 100, 500, 10_000].each { fieldsCount ->

            keyValueLoader.load(fieldsCount) each { keyValue ->

                Operation[] pojo = jsonToKeyValueConverter.fromKeyValue(keyValue, Operation[].class)
                log.info "Result: ${JsonOutput.prettyPrint(jacksonConverter.toJson(pojo))}"
            }
        }
    }

    @Test
    void test() throws Exception {

        [20, 100, 500, 10_000].each { fieldsCount ->
            jsonLoader.load(fieldsCount) each { json ->

                Map<String, String> keyValue = jsonToKeyValueConverter.toKeyValue(json)
                Operation[] pojo = jsonToKeyValueConverter.fromKeyValue(keyValue, Operation[].class)

                assertEquals(JsonOutput.prettyPrint(json),
                        JsonOutput.prettyPrint(jacksonConverter.toJson(pojo)))
            }
        }
    }
}
