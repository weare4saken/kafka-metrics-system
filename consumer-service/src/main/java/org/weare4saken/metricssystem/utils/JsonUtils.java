package org.weare4saken.metricssystem.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import org.weare4saken.metricssystem.ConsumerServiceApplication;

import java.util.Map;

@UtilityClass
public class JsonUtils {

    public static ObjectMapper getObjectMapper() {
        return ConsumerServiceApplication.getApplicationContext().getBean(ObjectMapper.class);
    }

    public static boolean isObject(String rawJson) {
        try {
            return JsonUtils.getObjectMapper().readTree(rawJson).isObject();
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    public Map<String, Object> readAsMap(String rawJson) throws JsonProcessingException {
        return JsonUtils.getObjectMapper().readValue(rawJson, new TypeReference<>() {
        });
    }
}
