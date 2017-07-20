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
    JsonLoader jsonLoader
    @Autowired
    KeyValueLoader keyValueLoader
    @Autowired
    PojoConverter converter

    @Test
    void testPojoToKeyValue() throws Exception {

        jsonLoader.loadAll().each { json ->

            Map<String, String> map = converter.convertJsonToKeyValue(json)
            def sb = new StringBuilder("Result map:")

            map.each { key, value ->
                sb << key << '=' << value << '\n'
            }

            log.info sb.toString()
        }
    }

    @Test
    void testKeyValueToPojo() throws Exception {

        [20, 100, 500, 10_000].each { fieldsCount ->

            keyValueLoader.load(fieldsCount) each { keyValue ->

                Operation[] pojo = converter.convertKeyValueToPojo(keyValue)
                log.info "Result: ${JsonOutput.prettyPrint(converter.convertPojoToJson(pojo))}"
            }
        }
    }

    @Test
    void test() throws Exception {

        [20, 100, 500, 10_000].each { fieldsCount ->
            jsonLoader.load(fieldsCount) each { json ->

                Map<String, String> keyValue = converter.convertJsonToKeyValue(json)
                Operation[] pojo = converter.convertKeyValueToPojo(keyValue)

                assertEquals(
                        JsonOutput.prettyPrint(json),
                        JsonOutput.prettyPrint(converter.convertPojoToJson(pojo))
                )
            }
        }
    }
}
