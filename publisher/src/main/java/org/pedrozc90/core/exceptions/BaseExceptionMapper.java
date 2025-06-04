package org.pedrozc90.core.exceptions;

import io.quarkus.runtime.LaunchMode;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.pedrozc90.core.mappers.ConstraintViolationMapper;
import org.pedrozc90.core.objects.ErrorMessage;

public abstract class BaseExceptionMapper<E extends Throwable> implements ExceptionMapper<E> {

    @Inject
    protected ConstraintViolationMapper mapper;

    protected Response createResponse(final Response.Status status, final Object entity) {
        return Response
            .status(status)
            .type(MediaType.APPLICATION_JSON)
            .entity(entity)
            .build();
    }

    protected Response createResponse(final Response.Status status, final Throwable reason) {
        final String error = reason.getClass().getName();
        final String message = reason.getMessage();
        final String stack = LaunchMode.isDev() ? ExceptionUtils.getStackTrace(reason) : null;

        final ErrorMessage entity = new ErrorMessage();
        entity.setStatus(status.getStatusCode());
        entity.setError(error);
        entity.setMessage(message);
        entity.setStack(stack);

        return createResponse(status, entity);
    }

    protected Response createResponse(final WebApplicationException reason) {
        final String message = reason.getMessage();
        final Response response = reason.getResponse();
        final Response.Status status = Response.Status.fromStatusCode(response.getStatus());
        final Object entity = response.getEntity();

        if (entity != null) {
            return response;
        }

        return createResponse(status, reason);
    }

    protected Response createResponse(final ConstraintViolationException reason) {
        final ErrorMessage entity = mapper.toDto(reason);
        return createResponse(Response.Status.BAD_REQUEST, entity);
    }

}
