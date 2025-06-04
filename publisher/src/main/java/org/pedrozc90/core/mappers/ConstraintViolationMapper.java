package org.pedrozc90.core.mappers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.StringUtils;
import org.pedrozc90.core.objects.ErrorMessage;
import org.pedrozc90.core.objects.Violation;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ConstraintViolationMapper {

    public ErrorMessage toDto(final ConstraintViolationException exception) {
        final List<Violation> violations = exception.getConstraintViolations().stream()
            .map(Violation::of)
            .sorted((a, b) -> {
                if (StringUtils.equals(a.getField(), b.getField())) return a.getReason().compareTo(b.getReason());
                return a.getField().compareTo(b.getField());
            })
            .collect(Collectors.toList());

        return ErrorMessage.builder()
            .message("Oops, we have a validation problem!")
            .violations(violations)
            .build();
    }

}
