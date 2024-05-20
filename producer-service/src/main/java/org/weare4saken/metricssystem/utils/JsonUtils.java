package org.weare4saken.metricssystem.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import org.weare4saken.ProducerServiceApplication;

@UtilityClass
public class JsonUtils {

    public static ObjectMapper getObjectMapper() {
        return ProducerServiceApplication.getApplicationContext().getBean(ObjectMapper.class);
    }

    public String stringify(Object object) throws JsonProcessingException {
        return JsonUtils.getObjectMapper().writeValueAsString(object);
    }
}
