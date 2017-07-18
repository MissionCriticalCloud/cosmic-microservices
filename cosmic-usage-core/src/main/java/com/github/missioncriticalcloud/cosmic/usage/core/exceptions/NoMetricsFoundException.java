package com.github.missioncriticalcloud.cosmic.usage.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoMetricsFoundException extends RuntimeException {

    public NoMetricsFoundException() {
        super("No metrics found");
    }

    public NoMetricsFoundException(final String message) {
        super(message);
    }

    public NoMetricsFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public NoMetricsFoundException(final Throwable cause) {
        super(cause);
    }
}
