package org.pedrozc90.core.objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Field;

@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class Violation implements Serializable {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "field")
    private String field;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "reason")
    private String reason;

    public static Violation of(final String field, final String reason) {
        final Violation violation = new Violation();
        violation.field = field;
        violation.reason = reason;
        return violation;
    }

    public static Violation of(final ConstraintViolation<?> violation) {
        final String field = extractField(violation);
        final String message = getMessage(violation);
        return of(field, message);
    }

    private static String getField(final ConstraintViolation<?> violation) {
        final String[] segments = violation.getPropertyPath().toString().split("\\.");
        return segments[segments.length - 1];
    }

    private static String getMessage(final ConstraintViolation<?> violation) {
        return violation.getMessage();
    }

    private static String extractField(final ConstraintViolation<?> violation) {
        final Path path = violation.getPropertyPath();

        // for @AssertTrue methods, find the corresponding field's @JsonProperty value
        if (path.toString().startsWith("valid") || path.toString().startsWith("is")) {
            final Class<?> rootBeanClass = violation.getRootBeanClass();
            final String methodName = path.toString();
            String fieldName = methodName.replaceFirst("^(valid|is)", "");
            fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);

            try {
                // look for a field with matching name pattern
                for (Field field : rootBeanClass.getDeclaredFields()) {
                    JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
                    if (jsonProperty != null &&
                        (field.getName().equalsIgnoreCase(fieldName) ||
                            jsonProperty.value().equalsIgnoreCase(fieldName))) {
                        return jsonProperty.value();
                    }
                }
                return fieldName; // If no matching field with @JsonProperty is found
            } catch (Exception e) {
                return fieldName; // If anything goes wrong, fall back to the field name
            }
        }

        // for regular field validations, check if the leaf node has @JsonProperty
        Path.Node leafNode = null;
        StringBuilder pathBuilder = new StringBuilder();

        for (Path.Node node : path) {
            final String name = node.getName();
            if (StringUtils.isBlank(name)) break;

            leafNode = node;

            if (!pathBuilder.isEmpty()) {
                pathBuilder.append(".");
            }
            pathBuilder.append(name);
        }

        if (leafNode != null) {
            try {
                Field field = violation.getRootBeanClass().getDeclaredField(leafNode.getName());
                JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
                if (jsonProperty != null && !jsonProperty.value().isEmpty()) {
                    String fullPath = pathBuilder.toString();
                    return fullPath.substring(0, fullPath.lastIndexOf(leafNode.getName())) + jsonProperty.value();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                // field not found, use the original path
            }
        }

        return pathBuilder.toString();
    }

}
