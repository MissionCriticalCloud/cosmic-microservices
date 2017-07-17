package com.github.missioncriticalcloud.cosmic.usage.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class UnableToSearchMetricsException extends RuntimeException {

    public UnableToSearchMetricsException() {
        super("Unable to search metrics");
    }

    public UnableToSearchMetricsException(final String message) {
        super(message);
    }

    public UnableToSearchMetricsException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UnableToSearchMetricsException(final Throwable cause) {
        super(cause);
    }
}
