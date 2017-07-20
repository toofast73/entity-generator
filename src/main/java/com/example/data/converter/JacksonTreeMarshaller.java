package com.example.data.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
@Service
class JacksonTreeMarshaller implements PojoToKeyValueMarshaller {

    @Autowired
    private JacksonMarshaller jacksonMarshaller;

    private String POINT = ".";
    private String OPEN = "[";
    private String CLOSE = "]";

    @Override
    public Map<String, String> toKeyValue(String json) {

        Map<String, String> map = new LinkedHashMap<>();
        addKeys("", jacksonMarshaller.readTree(json), map);
        return map;
    }

    private void addKeys(String currentPath, JsonNode jsonNode, Map<String, String> map) {
        if (jsonNode.isObject()) {
            ObjectNode objectNode = (ObjectNode) jsonNode;
            Iterator<Map.Entry<String, JsonNode>> iter = objectNode.fields();
            String pathPrefix = currentPath.isEmpty() ? "" : currentPath + POINT;

            while (iter.hasNext()) {
                Map.Entry<String, JsonNode> entry = iter.next();
                addKeys(pathPrefix + entry.getKey(), entry.getValue(), map);
            }
        } else if (jsonNode.isArray()) {
            ArrayNode arrayNode = (ArrayNode) jsonNode;
            for (int i = 0; i < arrayNode.size(); i++) {
                addKeys(currentPath + OPEN + i + CLOSE, arrayNode.get(i), map);
            }
        } else if (jsonNode.isValueNode()) {
            ValueNode valueNode = (ValueNode) jsonNode;
            map.put(currentPath, valueNode.asText());
        }
    }

    /**
     * в названии колонок не может быть . и [ ]
     */
    void cqlMode(boolean b) {
        if (b) {
            POINT = "POINT";
            OPEN = "OPEN";
            CLOSE = "CLOSE";
        } else {
            POINT = ".";
            OPEN = "[";
            CLOSE = "]";
        }
    }
}
