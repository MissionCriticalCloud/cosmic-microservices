package com.github.missioncriticalcloud.cosmic.usage.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class TokenException extends RuntimeException {

    public TokenException() {
        super("Not authorized to view this resource!");
    }

    public TokenException(final String message) {
        super(message);
    }

    public TokenException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public TokenException(final Throwable cause) {
        super(cause);
    }
}
