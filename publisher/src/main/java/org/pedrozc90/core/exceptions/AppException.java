package org.pedrozc90.core.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class AppException extends WebApplicationException {

    private AppException(final Response.Status status, final String message) {
        super(message, status);
    }

    private AppException(final Response.Status status, final Throwable cause) {
        super(cause, status);
    }

    public static AppException of(final Response.Status status, final String message) {
        return new AppException(status, message);
    }

    public static AppException of(final Response.Status status, final Throwable cause) {
        return new AppException(status, cause);
    }
}
