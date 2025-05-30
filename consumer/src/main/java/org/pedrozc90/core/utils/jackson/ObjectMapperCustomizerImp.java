package org.pedrozc90.core.utils.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;
import io.quarkus.jackson.ObjectMapperCustomizer;
import jakarta.inject.Singleton;

/**
 * reference: https://quarkus.io/guides/rest-json#jackson
 */
@Singleton
public class ObjectMapperCustomizerImp implements ObjectMapperCustomizer {

    public void customize(final ObjectMapper mapper) {
        // output json with indentation
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        // fix boolean coercion rules, where it parses integer as boolean. (0 -> false, non-zero -> true)
        mapper.coercionConfigFor(LogicalType.Boolean)
            .setCoercion(CoercionInputShape.Integer, CoercionAction.Fail)
            .setCoercion(CoercionInputShape.String, CoercionAction.TryConvert); // or Fail
    }

}
