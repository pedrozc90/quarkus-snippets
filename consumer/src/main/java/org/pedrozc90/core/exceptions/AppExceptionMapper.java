package org.pedrozc90.core.exceptions;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(value = Priorities.USER)
public class AppExceptionMapper extends BaseExceptionMapper<AppException> {

    @Override
    public Response toResponse(final AppException reason) {
        return createResponse(reason);
    }

}
