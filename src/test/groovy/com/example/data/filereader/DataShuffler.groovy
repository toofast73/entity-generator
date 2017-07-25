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
    void randomize() {

        [20, 100, 500, 10_000].each { fieldsCount ->
            pojoLoader.load(fieldsCount).each { Operation[] pojo ->

                pojo.each { Operation op ->
                    op.setId(RandomStringUtils.randomAlphanumeric(24))
                    op.setIndex(RandomUtils.nextInt(0, 10))
                    op.setGuid(RandomStringUtils.randomAlphanumeric(36))
                    op.setIsActive(RandomUtils.nextBoolean())
                    op.setBalance(RandomStringUtils.randomNumeric(10))
                    op.setPicture("http://placehold.it/" + RandomStringUtils.randomAlphanumeric(20))
                    op.setAge(RandomUtils.nextInt(18, 100))
                    op.setEyeColor(randomWords(10))
                    op.setName(randomWords(20))
                    op.setGender(RandomStringUtils.randomAlphabetic(1))
                    op.setCompany(randomWords(100))
                    op.setEmail(RandomStringUtils.randomAlphanumeric(20) + "@mail.ru")
                    op.setPhone(RandomStringUtils.randomNumeric(10))
                    op.setAddress(randomWords(200))
                    op.setAbout(randomWords(1000))
                    op.setRegistered(randomWords(20))
                    op.setLatitude(RandomUtils.nextDouble(0.0, 360.0))
                    op.setLongitude(RandomUtils.nextDouble(0.0, 360.0))
                    op.tags[0] = randomWords(50)
                    op.tags[1] = randomWords(25)
                }
                def json = prettyPrint(converter.convertPojoToJson(pojo))
                log.info "JSON: $json\n"

                Files.write(Paths.get("01.01.01_${fieldsCount}f.json"), [json], StandardOpenOption.CREATE)
            }
        }
    }


    String randomWords(int textLength) {

        def sb = new StringBuilder()
        List<String> words = getWords(1000)

        while (sb.length() < textLength) {
            sb.append(words[RandomUtils.nextInt(0, words.size())]).append(' ')
        }
        sb.deleteCharAt(sb.length() - 1).toString()
    }

    List<String> list

    List<String> getWords(Integer count) {
        if (list) {
            return list
        }

        list = []
        count.times {
            def length = RandomUtils.nextInt(3, 10)
            list << RandomStringUtils.randomAlphabetic(length)
        }
        return list
    }
}
