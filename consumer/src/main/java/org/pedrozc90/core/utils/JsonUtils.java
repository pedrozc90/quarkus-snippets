package org.pedrozc90.core.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.lang3.StringUtils;

@ApplicationScoped
public class JsonUtils {

    @Inject
    protected ObjectMapper mapper;

    public <T> String toString(final T obj) throws JsonProcessingException {
        if (obj == null) return null;
        return mapper.writeValueAsString(obj);
    }

    public <T> T toObject(final String value, final Class<T> clazz) throws IllegalArgumentException, JsonProcessingException {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("Input value cannot be null or empty");
        }
        return mapper.readValue(value, clazz);
    }

}
