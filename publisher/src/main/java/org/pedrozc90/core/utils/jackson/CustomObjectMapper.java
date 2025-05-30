package org.pedrozc90.core.utils.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.quarkus.arc.All;
import io.quarkus.jackson.ObjectMapperCustomizer;

import java.util.List;

/**
 * reference: https://quarkus.io/guides/rest-json#jackson
 */
public class CustomObjectMapper {

    private ObjectMapper myObjectMapper() {
        final ObjectMapper mapper = new ObjectMapper();

        // Enable/disable features
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Register Java 8 Date/Time support
        mapper.registerModule(new JavaTimeModule());

        return mapper;
    }

    // replaces the CDI producer for ObjectMapper built into Quarkus
    // @Singleton
    // @Produces
    public ObjectMapper objectMapper(@All final List<ObjectMapperCustomizer> customizers) {
        final ObjectMapper mapper = myObjectMapper(); // Custom `ObjectMapper`

        // apply all ObjectMapperCustomizer beans (incl. Quarkus)
        for (ObjectMapperCustomizer customizer : customizers) {
            customizer.customize(mapper);
        }

        return mapper;
    }

}
