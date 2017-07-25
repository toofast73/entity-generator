package com.example.data.filereader

import com.example.Start
import com.example.data.converter.PojoConverter
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
class PojoLoaderTest {

    @Autowired
    private JsonLoader jsonLoader
    @Autowired
    private PojoLoader pojoLoader
    @Autowired
    private PojoConverter converter

    @Test
    void testLoad() {

        List<String> jsons = jsonLoader.loadAll()
        List<Operation[]> pojos = pojoLoader.loadAll()

        for (int i = 0; i < pojos.size(); i++) {

            assertEquals(
                    JsonOutput.prettyPrint(jsons.get(i)),
                    JsonOutput.prettyPrint(converter.convertPojoToJson(pojos.get(i)))
            )
        }
    }

    @Test
    void testLoadWithSpecifiedFieldsCount() {

        [20, 100, 500, 10_000].each { fieldsCount ->

            List<String> jsons = jsonLoader.load(fieldsCount)
            List<Operation[]> pojos = pojoLoader.load(fieldsCount)

            for (int i = 0; i < pojos.size(); i++) {

                assertEquals(
                        JsonOutput.prettyPrint(jsons.get(i)),
                        JsonOutput.prettyPrint(converter.convertPojoToJson(pojos.get(i)))
                )
            }
        }
    }
}
