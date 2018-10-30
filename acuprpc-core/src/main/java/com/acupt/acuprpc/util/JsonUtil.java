package com.acupt.acuprpc.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

/**
 * Created by shichengwei on 17/07/2017.
 */
public class JsonUtil {

    private static final ThreadLocal<ObjectMapper> objectMapperThreadLocal = ThreadLocal.withInitial(() -> new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false));

    private JsonUtil() {
    }

    @SneakyThrows
    public static String toJson(Object object) {
        ObjectMapper objectMapper = objectMapperThreadLocal.get();
        return objectMapper.writeValueAsString(object);
    }

    @SneakyThrows
    public static <T> T fromJson(String json, JavaType type) {
        ObjectMapper objectMapper = objectMapperThreadLocal.get();
        return objectMapper.readValue(json, type);
    }

    @SneakyThrows
    public static <T> T fromJson(String json, TypeReference type) {
        ObjectMapper objectMapper = objectMapperThreadLocal.get();
        return objectMapper.readValue(json, type);
    }
}
