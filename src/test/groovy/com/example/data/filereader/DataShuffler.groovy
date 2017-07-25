package com.example.data.filereader

import com.example.Start
import com.example.data.converter.PojoConverter
import com.example.data.pojo.Operation
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.apache.commons.lang.RandomStringUtils
import org.apache.commons.lang3.RandomUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

import static groovy.json.JsonOutput.prettyPrint

/**
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Start.class)
@CompileStatic
@Slf4j
class DataShuffler {

    @Autowired
    private JsonLoader jsonLoader
    @Autowired
    private PojoLoader pojoLoader
    @Autowired
    private PojoConverter converter


    @Test
    void shuffle() {

        [10_000].each { fieldsCount ->
            pojoLoader.load(fieldsCount).each { Operation[] pojo ->

                pojo.each { Operation op ->
                    op.setId(RandomStringUtils.randomAlphanumeric(24))
                    op.setIndex(RandomUtils.nextInt(0, 10))
                    op.setGuid(RandomStringUtils.randomAlphanumeric(36))
                    op.setIsActive(RandomUtils.nextBoolean())
                    op.setBalance(RandomStringUtils.randomNumeric(10))
                    op.setPicture("http://placehold.it/" + RandomStringUtils.randomAlphanumeric(20))
                    op.setAge(RandomUtils.nextInt(18, 100))
                    op.setEyeColor(RandomStringUtils.randomAlphabetic(10))
                    op.setName(RandomStringUtils.randomAlphabetic(20))
                    op.setGender(RandomStringUtils.randomAlphabetic(1))
                    op.setCompany(RandomStringUtils.randomAlphabetic(100))
                    op.setEmail(RandomStringUtils.randomAlphanumeric(20) + "@mail.ru")
                    op.setPhone(RandomStringUtils.randomNumeric(10))
                    op.setAddress(RandomStringUtils.randomAlphabetic(200))
                    op.setAbout(RandomStringUtils.randomAscii(1000))
                    op.setRegistered(RandomStringUtils.randomNumeric(20))
                    op.setLatitude(RandomUtils.nextDouble(0.0, 360.0))
                    op.setLongitude(RandomUtils.nextDouble(0.0, 360.0))
                    op.tags[0] = RandomStringUtils.randomAscii(50)
                    op.tags[1] = RandomStringUtils.randomAlphabetic(25)
                }
                def json = prettyPrint(converter.convertPojoToJson(pojo))
                log.info "JSON: $json\n"

                Files.write(Paths.get("tratra"), [json], StandardOpenOption.CREATE)
            }
        }
    }
}
