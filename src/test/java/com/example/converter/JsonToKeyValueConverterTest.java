package com.example.converter;

import com.example.Start;
import com.example.data.filereader.JsonLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

/**
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Start.class)
public class JsonToKeyValueConverterTest {
    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private JsonToKeyValueConverter jsonToKeyValueConverter;
    @Autowired
    private JsonLoader jsonLoader;

    @Test
    public void test() throws Exception {


        List<String> operations = jsonLoader.load();

        operations.forEach(json -> {

            Map<String, String> map = jsonToKeyValueConverter.convert(json);
            StringBuilder sb = new StringBuilder("Result map:");

            map.forEach((key, value) -> {
                sb.append(key)
                        .append('=')
                        .append(value)
                        .append('\n');
            });

            logger.info(sb.toString());
        });
    }
}
