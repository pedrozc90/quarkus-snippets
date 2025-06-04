package org.pedrozc90.core.exceptions;

import jakarta.annotation.Priority;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(value = Priorities.USER)
public class ConstraintViolationExceptionMapper extends BaseExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(final ConstraintViolationException reason) {
        return createResponse(reason);
    }

}
